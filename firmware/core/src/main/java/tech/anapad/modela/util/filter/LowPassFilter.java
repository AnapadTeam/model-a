package tech.anapad.modela.util.filter;

import static java.lang.Double.MAX_VALUE;

/**
 * {@link LowPassFilter} is a rate-independent, low-pass filtering algorithm.
 *
 * @see <a href="http://phrogz.net/js/framerate-independent-low-pass-filter.html">Low Pass Filter</a>
 */
public class LowPassFilter {

    private final double smoothingFactor;

    private double value;

    /**
     * Instantiates a new {@link LowPassFilter}.
     *
     * @param smoothingFactor the smoothing factor
     */
    public LowPassFilter(double smoothingFactor) {
        this.smoothingFactor = smoothingFactor;
        value = MAX_VALUE;
    }

    /**
     * Applies the filtering algorithm to this {@link LowPassFilter}.
     *
     * @param newValue the new value
     *
     * @return the filter output value (same as {@link #getValue()})
     */
    public double filter(double newValue) {
        if (value == MAX_VALUE) {
            value = newValue;
        } else {
            value += (newValue - value) / smoothingFactor;
        }
        return value;
    }

    /**
     * @see #filter(double)
     */
    public double getValue() {
        return value;
    }
}
