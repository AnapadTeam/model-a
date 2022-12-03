package tech.anapad.modela.util.filter;

import static java.lang.System.currentTimeMillis;

/**
 * {@link LowPassFilter} is a rate-independent, low-pass filtering algorithm.
 *
 * @see <a href="http://phrogz.net/js/framerate-independent-low-pass-filter.html">Low Pass Filter</a>
 */
public class LowPassFilter {

    private final double smoothingFactor;

    private double value;
    private long lastTimeMillis;

    /**
     * Instantiates a new {@link LowPassFilter}.
     *
     * @param smoothingFactor the smoothing factor
     */
    public LowPassFilter(double smoothingFactor) {
        this.smoothingFactor = smoothingFactor;
    }

    /**
     * Applies the filtering algorithm to this {@link LowPassFilter}.
     *
     * @param newValue the new value
     *
     * @return the filter output value (same as {@link #getValue()})
     */
    public double filter(double newValue) {
        long currentMillis = currentTimeMillis();
        double elapsedMillis = currentMillis - lastTimeMillis;
        value += elapsedMillis * (newValue - value) / smoothingFactor;
        lastTimeMillis = currentMillis;
        return value;
    }

    /**
     * @see #filter(double)
     */
    public double getValue() {
        return value;
    }
}
