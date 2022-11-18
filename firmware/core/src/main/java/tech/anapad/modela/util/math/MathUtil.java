package tech.anapad.modela.util.math;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * {@link MathUtil} contains utility functions for math.
 */
public final class MathUtil {

    /**
     * Clamps an integer.
     *
     * @param value the integer value to clamp
     * @param min   the min
     * @param max   the max
     *
     * @return the clamped integer
     */
    public static int clamp(int value, int min, int max) {
        return max(min, min(max, value));
    }
}
