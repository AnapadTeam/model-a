package tech.anapad.modela.hapticsboard.ioportexpander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.math.BitUtil.setBit;

/**
 * {@link IOPortExpander} represents the TCA9534 IO Port Expander chip.
 */
public class IOPortExpander {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOPortExpander.class);
    private static final short TCA9534_REGISTER_OUTPUT_PORT = 0x01;
    private static final short TCA9534_REGISTER_CONFIGURATION = 0x03;

    private final int index;
    private final short address;
    private byte outputRegister;

    /**
     * Instantiates a new {@link IOPortExpander}.
     *
     * @param index   the chip index
     * @param address the I2C address
     */
    public IOPortExpander(int index, short address) {
        this.index = index;
        this.address = address;
    }

    /**
     * Configures this {@link IOPortExpander} as an output with "low" as the default driven output.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void configure(int i2cFD) throws Exception {
        zeroOutput(i2cFD);
        LOGGER.info("{}: zeroed the output register.", index);
        writeRegisterByte(i2cFD, address, TCA9534_REGISTER_CONFIGURATION, (byte) 0x00, true);
        LOGGER.info("{}: configured pins for output.", index);
    }

    /**
     * Sets an output pin of this {@link IOPortExpander} at the given <code>index</code>.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void setOutput(int i2cFD, int index) throws Exception {
        outputRegister = (byte) setBit(outputRegister, 1, index);
        writeRegisterByte(i2cFD, address, TCA9534_REGISTER_OUTPUT_PORT, outputRegister, true);
    }

    /**
     * Resets an output pin of this {@link IOPortExpander} at the given <code>index</code>.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void resetOutput(int i2cFD, int index) throws Exception {
        outputRegister = (byte) setBit(outputRegister, 0, index);
        writeRegisterByte(i2cFD, address, TCA9534_REGISTER_OUTPUT_PORT, outputRegister, true);
    }

    /**
     * Zeros the output pins of this {@link IOPortExpander}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void zeroOutput(int i2cFD) throws Exception {
        outputRegister = 0x00;
        writeRegisterByte(i2cFD, address, TCA9534_REGISTER_OUTPUT_PORT, outputRegister, true);
    }

    public int getIndex() {
        return index;
    }

    public short getI2CAddress() {
        return address;
    }

    public byte getOutputRegister() {
        return outputRegister;
    }
}
