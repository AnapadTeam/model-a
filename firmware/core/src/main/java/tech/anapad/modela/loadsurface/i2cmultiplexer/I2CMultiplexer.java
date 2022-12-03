package tech.anapad.modela.loadsurface.i2cmultiplexer;

import static tech.anapad.modela.util.i2c.I2CNative.writeByte;

/**
 * {@link I2CMultiplexer} represents the TCA9544A 4-channel I2C multiplexer chip.
 */
public class I2CMultiplexer {

    private static final short TCA9544A_ADDRESS = 0x70;

    private final int i2cFD;

    private Channel currentChannel;

    /**
     * Instantiates a new {@link I2CMultiplexer}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     */
    public I2CMultiplexer(int i2cFD) {
        this.i2cFD = i2cFD;
    }

    /**
     * Disables this {@link I2CMultiplexer}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void disable() throws Exception {
        writeByte(i2cFD, TCA9544A_ADDRESS, (byte) 0x00);
    }

    /**
     * Sets the active {@link Channel} of this {@link I2CMultiplexer}.
     *
     * @param channel the {@link Channel}
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void setChannel(Channel channel) throws Exception {
        final byte controlRegister = switch (channel) {
            case _0 -> 0b100;
            case _1 -> 0b101;
            case _2 -> 0b110;
            case _3 -> 0b111;
        };
        writeByte(i2cFD, TCA9544A_ADDRESS, controlRegister);
        currentChannel = channel;
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }
}
