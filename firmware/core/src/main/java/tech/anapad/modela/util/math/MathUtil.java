package tech.anapad.modela.util.math;

/**
 * {@link MathUtil} contains utility functions for math.
 */
public class MathUtil {

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
        return Math.max(min, Math.min(max, value));
    }
}
