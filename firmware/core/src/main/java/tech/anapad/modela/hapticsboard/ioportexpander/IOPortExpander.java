package tech.anapad.modela.hapticsboard.ioportexpander;

import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.math.BitUtil.setBit;

/**
 * {@link IOPortExpander} represents the TCA9534 IO Port Expander chip.
 */
public class IOPortExpander {

    private static final short TCA9534_REGISTER_OUTPUT_PORT = 0x01;
    private static final short TCA9534_REGISTER_CONFIGURATION = 0x03;

    private final int i2cFD;
    private final int index;
    private final short address;
    private byte outputRegister;

    /**
     * Instantiates a new {@link IOPortExpander}.
     *
     * @param i2cFD   the low-level I2C device file descriptor
     * @param index   the chip index
     * @param address the I2C address
     */
    public IOPortExpander(int i2cFD, int index, short address) {
        this.i2cFD = i2cFD;
        this.index = index;
        this.address = address;
    }

    /**
     * Configures this {@link IOPortExpander} as an output with "low" as the default driven output.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void configure() throws Exception {
        zeroOutput();
        writeRegisterByte(i2cFD, address, TCA9534_REGISTER_CONFIGURATION, (byte) 0x00, true);
    }

    /**
     * Sets an output pin of this {@link IOPortExpander} at the given <code>index</code>.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void setOutput(int index) throws Exception {
        outputRegister = (byte) setBit(outputRegister, 1, index);
        writeOutputRegister();
    }

    /**
     * Resets an output pin of this {@link IOPortExpander} at the given <code>index</code>.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void resetOutput(int index) throws Exception {
        outputRegister = (byte) setBit(outputRegister, 0, index);
        writeOutputRegister();
    }

    /**
     * Zeros the output pins of this {@link IOPortExpander}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void zeroOutput() throws Exception {
        outputRegister = 0x00;
        writeOutputRegister();
    }

    /**
     * Writes {@link #outputRegister} to {@link #TCA9534_REGISTER_OUTPUT_PORT}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void writeOutputRegister() throws Exception {
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

    public void setOutputRegister(byte outputRegister) {
        this.outputRegister = outputRegister;
    }
}
