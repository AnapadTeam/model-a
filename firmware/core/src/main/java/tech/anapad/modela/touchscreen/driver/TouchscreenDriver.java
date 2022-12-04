package tech.anapad.modela.touchscreen.driver;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.copyOf;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterByte;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterBytes;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterBytes;
import static tech.anapad.modela.util.math.BitUtil.getBit;
import static tech.anapad.modela.util.math.BitUtil.getBits;
import static tech.anapad.modela.util.math.MathUtil.clamp;

/**
 * {@link TouchscreenDriver} represents the GT9110 touchscreen driver chip.
 */
public class TouchscreenDriver {

    private static final short GT9110_I2C_ADDRESS = 0x5D;
    private static final short GT9110_REGISTER_RESOLUTION = (short) 0x8146;
    private static final short GT9110_REGISTER_STATUS = (short) 0x814E;
    private static final short GT9110_REGISTER_TOUCHES_START = (short) 0x814F;
    private static final short GT9110_REGISTER_CONFIG_START = (short) 0x8047;
    private static final short GT9110_REGISTER_CONFIG_END = (short) 0x80FE;
    private static final short GT9110_REGISTER_CONFIG_LENGTH = GT9110_REGISTER_CONFIG_END -
            GT9110_REGISTER_CONFIG_START + 1;
    private static final int GT9110_TOUCH_CAPACITY = 10;
    private static final int GT9110_TOUCH_REGISTER_LENGTH = 8; // Each touch has 8 bytes of data
    private static final int GT9110_TOTAL_TOUCH_DATA_LENGTH = GT9110_TOUCH_REGISTER_LENGTH * GT9110_TOUCH_CAPACITY;

    private final int i2cFD;

    private Resolution resolution;
    private boolean neverSampled;

    /**
     * Instantiates a new {@link TouchscreenDriver}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     */
    public TouchscreenDriver(int i2cFD) {
        this.i2cFD = i2cFD;
        neverSampled = true;
    }

    /**
     * Reads the current {@link TouchscreenDriver} {@link Configuration}.
     *
     * @return the {@link Configuration}
     * @throws Exception thrown for {@link Exception}
     */
    public Configuration readConfiguration() throws Exception {
        return new Configuration(readRegisterBytes(i2cFD, GT9110_I2C_ADDRESS, GT9110_REGISTER_CONFIG_START,
                GT9110_REGISTER_CONFIG_LENGTH, false));
    }

    /**
     * Writes the given {@link Configuration} to the {@link TouchscreenDriver}.
     *
     * @param configuration the {@link Configuration}
     *
     * @throws Exception thrown for {@link Exception}
     */
    public void writeConfiguration(Configuration configuration) throws Exception {
        int checksumSum = 0;
        for (int index = 0; index < configuration.getBytes().length; index++) {
            checksumSum += configuration.getBytes()[index] & 0xFF;
        }
        final byte checksum = (byte) ((~((byte) (checksumSum & 0xFF))) + 1);

        final byte[] newConfigurationBytes = copyOf(configuration.getBytes(), GT9110_REGISTER_CONFIG_LENGTH + 2);
        newConfigurationBytes[GT9110_REGISTER_CONFIG_LENGTH] = checksum;
        newConfigurationBytes[GT9110_REGISTER_CONFIG_LENGTH + 1] = 0x01; // "Fresh" config value
        writeRegisterBytes(i2cFD, GT9110_I2C_ADDRESS, GT9110_REGISTER_CONFIG_START, newConfigurationBytes, false);
    }

    /**
     * Returns {@link #readResolution()} if {@link #getLastReadResolution()} returns <code>null</code>.
     *
     * @return the {@link Resolution}
     * @throws Exception thrown for {@link Exception}
     */
    public Resolution getResolution() throws Exception {
        if (resolution == null) {
            resolution = readResolution();
        }
        return resolution;
    }

    /**
     * Reads the {@link Resolution} of the touchscreen.
     *
     * @return the {@link Resolution}
     * @throws Exception thrown for {@link Exception}
     */
    public Resolution readResolution() throws Exception {
        byte[] resolutionBytes = readRegisterBytes(i2cFD, GT9110_I2C_ADDRESS, GT9110_REGISTER_RESOLUTION, 4, false);
        final Resolution resolution = new Resolution((resolutionBytes[1] << 8) | resolutionBytes[0],
                (resolutionBytes[3] << 8) | resolutionBytes[2]);
        this.resolution = resolution;
        return resolution;
    }

    /**
     * Gets the last read {@link Resolution} when {@link #readResolution()} was called.
     *
     * @return the {@link Resolution}
     */
    public Resolution getLastReadResolution() {
        return resolution;
    }

    /**
     * Samples the touchscreen for touches. This also calls {@link #triggerSample()}.
     *
     * @return the {@link Touch}es {@link List} or <code>null</code> if the touchscreen is still in the process of
     * sampling
     * @throws Exception thrown for {@link Exception}
     */
    public List<Touch> sampleTouches() throws Exception {
        // Trigger an initial sample if called for the first time
        if (neverSampled) {
            triggerSample();
            neverSampled = false;
        }

        // Check if touchscreen data is ready to be read
        final byte coordinateStatusRegister = readRegisterByte(i2cFD,
                GT9110_I2C_ADDRESS, GT9110_REGISTER_STATUS, false);
        final boolean bufferReady = getBit(coordinateStatusRegister, 7) == 1;
        if (!bufferReady) {
            return null;
        }

        // Trigger another touchscreen sample
        triggerSample();

        // Read touchscreen touch bytes
        final int numberOfTouches = clamp(getBits(coordinateStatusRegister, 3, 0), 0, GT9110_TOUCH_CAPACITY);
        final byte[] touchBytes = readRegisterBytes(i2cFD, GT9110_I2C_ADDRESS,
                GT9110_REGISTER_TOUCHES_START, GT9110_TOTAL_TOUCH_DATA_LENGTH, false);

        // Loop through touches
        final List<Touch> touches = new ArrayList<>(numberOfTouches);
        for (int index = 0; index < numberOfTouches; index++) {
            final int arrayIndex = index * GT9110_TOUCH_REGISTER_LENGTH;
            final Touch touch = new Touch.Builder()
                    .id(touchBytes[arrayIndex] & 0xFF)
                    .x(getResolution().getX() -
                            (((touchBytes[arrayIndex + 2] & 0xFF) << 8) | (touchBytes[arrayIndex + 1] & 0xFF)))
                    .y(getResolution().getY() -
                            (((touchBytes[arrayIndex + 4] & 0xFF) << 8) | (touchBytes[arrayIndex + 3] & 0xFF)))
                    .size(((touchBytes[arrayIndex + 6] & 0xFF) << 8) | (touchBytes[arrayIndex + 5] & 0xFF))
                    .build();
            touches.add(touch);
        }
        return touches;
    }

    /**
     * Triggers a touchscreen sample.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    private void triggerSample() throws Exception {
        writeRegisterByte(i2cFD, GT9110_I2C_ADDRESS, GT9110_REGISTER_STATUS, (byte) 0, false);
    }
}
