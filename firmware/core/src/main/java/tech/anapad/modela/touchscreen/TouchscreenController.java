package tech.anapad.modela.touchscreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterByte;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterBytes;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.math.BitUtil.getBit;
import static tech.anapad.modela.util.math.BitUtil.getBits;
import static tech.anapad.modela.util.math.MathUtil.clamp;

/**
 * {@link TouchscreenController} is a controller for the GT9110 touchscreen driver board and PCAP panel.
 */
public class TouchscreenController implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouchscreenController.class);
    private static final int I2C_DEVICE_INDEX = 5;
    private static final short GT9110_I2C_ADDRESS = 0x5D;
    private static final short GT9110_REGISTER_RESOLUTION = (short) 0x8146;
    private static final short GT9110_REGISTER_STATUS = (short) 0x814E;
    private static final short GT9110_REGISTER_TOUCHES_START = (short) 0x814F;
    private static final int GT9110_TOUCH_REGISTER_LENGTH = 8; // Each touch has 8 bytes of data
    private static final int GT9110_TOTAL_TOUCH_DATA_LENGTH = GT9110_TOUCH_REGISTER_LENGTH * 10; // 10 touches
    private static final int MAX_READ_FAILURES = 100;

    private final ModelA modelA;
    private final List<Runnable> initializedListeners;
    private final List<Runnable> failureListeners;
    private final List<Consumer<Touch[]>> touchListeners;

    private Thread scanThread;
    private volatile boolean scanLoop;
    private Integer i2cFD;
    private int xResolution;
    private int yResolution;
    private int readFailures;

    /**
     * Instantiates a new {@link TouchscreenController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public TouchscreenController(ModelA modelA) {
        this.modelA = modelA;
        initializedListeners = new ArrayList<>();
        touchListeners = synchronizedList(new ArrayList<>());
        failureListeners = synchronizedList(new ArrayList<>());
        scanLoop = false;
        readFailures = 0;
    }

    /**
     * Starts {@link TouchscreenController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void start() throws Exception {
        LOGGER.info("Starting TouchscreenController...");

        LOGGER.info("Starting I2C-{}...", I2C_DEVICE_INDEX);
        i2cFD = I2CNative.start(I2C_DEVICE_INDEX);
        LOGGER.info("Started I2C-{}...", I2C_DEVICE_INDEX);

        LOGGER.info("Reading touchscreen resolution...");
        byte[] touchscreenResolutionBytes = readRegisterBytes(i2cFD,
                GT9110_I2C_ADDRESS, GT9110_REGISTER_RESOLUTION, 4, false);
        xResolution = (touchscreenResolutionBytes[1] << 8) | touchscreenResolutionBytes[0];
        yResolution = (touchscreenResolutionBytes[3] << 8) | touchscreenResolutionBytes[2];
        LOGGER.info("Read touchscreen resolution: {}x{}", xResolution, yResolution);

        LOGGER.info("Calling initialized listeners...");
        initializedListeners.forEach(Runnable::run);
        LOGGER.info("Called initialized listeners.");

        LOGGER.info("Starting scan thread...");
        scanLoop = true;
        scanThread = new Thread(this, "TouchscreenController Scan Thread");
        scanThread.start();
        LOGGER.info("Stopped scan thread.");

        LOGGER.info("Started TouchscreenController.");
    }

    /**
     * Stops {@link TouchscreenController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping TouchscreenController...");

        if (scanThread != null) {
            LOGGER.info("Stopping scan thread...");
            scanLoop = false;
            scanThread.join(1000);
            LOGGER.info("Stopped scan thread.");
        }

        if (i2cFD != null) {
            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped TouchscreenController.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This run loop is used for reading touchscreen data and passing it to listeners.
     */
    @Override
    public void run() {
        while (scanLoop) {
            // TODO implement a way to add a delay for power saving and such

            // Wait until touchscreen data is ready to be read
            byte coordinateStatusRegister = 0;
            boolean bufferReady;
            do {
                try {
                    coordinateStatusRegister = readRegisterByte(i2cFD,
                            GT9110_I2C_ADDRESS, GT9110_REGISTER_STATUS, false);
                    bufferReady = getBit(coordinateStatusRegister, 7) == 1;
                    if (!scanLoop) {
                        return;
                    }
                } catch (Exception ignored) {
                    LOGGER.warn("Failed to read from status register.");
                    readFailures++;
                    if (checkReadFailure()) {
                        return;
                    }
                    bufferReady = false;
                }
            } while (!bufferReady);

            // Reset buffer status to trigger another touchscreen sample
            try {
                writeRegisterByte(i2cFD, GT9110_I2C_ADDRESS, GT9110_REGISTER_STATUS, (byte) 0, false);
            } catch (Exception ignored) {
                LOGGER.warn("Failed to write to status register.");
                readFailures++;
                if (checkReadFailure()) {
                    return;
                }
            }

            // Read touchscreen touch bytes
            final int numberOfTouches = clamp(getBits(coordinateStatusRegister, 3, 0), 0, 10);
            final byte[] touchBytes;
            try {
                touchBytes = readRegisterBytes(i2cFD, GT9110_I2C_ADDRESS,
                        GT9110_REGISTER_TOUCHES_START, GT9110_TOTAL_TOUCH_DATA_LENGTH, false);
            } catch (Exception ignored) {
                LOGGER.warn("Failed to read touchscreen touches.");
                readFailures++;
                if (checkReadFailure()) {
                    return;
                }
                continue;
            }

            // Loop through touches and map to models
            final Touch[] touches = new Touch[numberOfTouches];
            for (int index = 0; index < touches.length; index++) {
                final int arrayIndex = index * GT9110_TOUCH_REGISTER_LENGTH;
                final Touch touch = new Touch();
                touch.setID(touchBytes[arrayIndex] & 0xFF);
                touch.setX(xResolution -
                        (((touchBytes[arrayIndex + 2] & 0xFF) << 8) | (touchBytes[arrayIndex + 1] & 0xFF)));
                touch.setY(yResolution -
                        (((touchBytes[arrayIndex + 4] & 0xFF) << 8) | (touchBytes[arrayIndex + 3] & 0xFF)));
                touch.setSize(((touchBytes[arrayIndex + 6] & 0xFF) << 8) | (touchBytes[arrayIndex + 5] & 0xFF));
                touches[index] = touch;
            }

            // Call touch listeners
            synchronized (touchListeners) {
                touchListeners.forEach(listener -> listener.accept(touches));
            }

            readFailures = 0;
        }
    }

    /**
     * Checks if {@link #readFailures} exceeded {@link #MAX_READ_FAILURES} and calls the {@link #failureListeners}.
     *
     * @return <code>true</code> if max was exceeded, <code>false</code> otherwise
     */
    private boolean checkReadFailure() {
        if (readFailures > MAX_READ_FAILURES) {
            LOGGER.error("Read failures exceeded {}!", MAX_READ_FAILURES);
            LOGGER.info("Stopping scan loop and calling failure listeners...");
            synchronized (failureListeners) {
                failureListeners.forEach(Runnable::run);
            }
            LOGGER.error("Called failure listeners.");
            return true;
        } else {
            return false;
        }
    }

    public List<Runnable> getInitializedListeners() {
        return initializedListeners;
    }

    public List<Runnable> getFailureListeners() {
        return failureListeners;
    }

    public List<Consumer<Touch[]>> getTouchListeners() {
        return touchListeners;
    }

    public int getXResolution() {
        return xResolution;
    }

    public int getYResolution() {
        return yResolution;
    }
}
