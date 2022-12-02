package tech.anapad.modela.touchscreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.touchscreen.driver.Resolution;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.touchscreen.driver.TouchscreenDriver;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;
import static tech.anapad.modela.touchscreen.driver.Configuration.NEW_CONFIGURATION;

/**
 * {@link TouchscreenController} is a controller for the GT9110 touchscreen driver board and PCAP panel.
 */
public class TouchscreenController implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouchscreenController.class);
    private static final int I2C_DEVICE_INDEX = 5;
    private static final int MAX_SAMPLE_FAILURES = 100;

    private final ModelA modelA;
    private final List<Runnable> configurationChangeListeners;
    private final List<Consumer<Resolution>> resolutionListeners;
    private final List<Consumer<Touch[]>> touchListeners;
    private final List<Runnable> failureListeners;

    private TouchscreenDriver touchscreenDriver;
    private Thread scanThread;
    private volatile boolean scanLoop;
    private Integer i2cFD;
    private int sampleFailures;

    /**
     * Instantiates a new {@link TouchscreenController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public TouchscreenController(ModelA modelA) {
        this.modelA = modelA;
        configurationChangeListeners = synchronizedList(new ArrayList<>());
        resolutionListeners = new ArrayList<>();
        touchListeners = synchronizedList(new ArrayList<>());
        failureListeners = synchronizedList(new ArrayList<>());
        scanLoop = false;
        sampleFailures = 0;
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

        touchscreenDriver = new TouchscreenDriver(i2cFD);
        LOGGER.info("Touchscreen resolution: {}x{}",
                touchscreenDriver.getResolution().getX(), touchscreenDriver.getResolution().getY());

        LOGGER.info("Checking for configuration difference...");
        if (Arrays.equals(touchscreenDriver.readConfiguration().getBytes(), NEW_CONFIGURATION.getBytes())) {
            LOGGER.info("No configuration differences.");
        } else {
            LOGGER.info("Configuration on chip is different! Programming new configuration...");
            touchscreenDriver.writeConfiguration(NEW_CONFIGURATION);
            LOGGER.info("Programmed new configuration. Calling configuration change listeners...");
            configurationChangeListeners.forEach(Runnable::run);
            LOGGER.info("Called configuration change listeners.");
        }

        LOGGER.info("Calling initialized listeners...");
        for (Consumer<Resolution> resolutionConsumer : resolutionListeners) {
            resolutionConsumer.accept(touchscreenDriver.getResolution());
        }
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

            // Sample touches and process failures
            final Touch[] touches;
            try {
                touches = touchscreenDriver.sampleTouches();
            } catch (Exception exception) {
                if (++sampleFailures > MAX_SAMPLE_FAILURES) {
                    LOGGER.error("Sample failures exceeded {}!", MAX_SAMPLE_FAILURES);
                    LOGGER.info("Stopping scan loop and calling failure listeners...");
                    synchronized (failureListeners) {
                        failureListeners.forEach(Runnable::run);
                    }
                    LOGGER.info("Called failure listeners.");
                    return;
                } else {
                    continue;
                }
            }
            sampleFailures = 0;
            if (touches == null) {
                continue;
            }

            // Call touch listeners
            synchronized (touchListeners) {
                touchListeners.forEach(listener -> listener.accept(touches));
            }
        }
    }

    public List<Runnable> getConfigurationChangeListeners() {
        return configurationChangeListeners;
    }

    public List<Consumer<Resolution>> getResolutionListeners() {
        return resolutionListeners;
    }

    public List<Consumer<Touch[]>> getTouchListeners() {
        return touchListeners;
    }

    public List<Runnable> getFailureListeners() {
        return failureListeners;
    }

    public TouchscreenDriver getTouchscreenDriver() {
        return touchscreenDriver;
    }
}
