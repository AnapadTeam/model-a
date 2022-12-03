package tech.anapad.modela.loadsurface.i2cmultiplexer;

/**
 * {@link Channel} represents a channel of the {@link I2CMultiplexer}.
 */
public enum Channel {

    _0,
    _1,
    _2,
    _3;

    @Override
    public String toString() {
        return super.toString().replace("_", "");
    }
}
