package tech.anapad.modela.touchscreen.driver;

/**
 * {@link Resolution} represents the touchscreen resolution.
 */
public class Resolution {

    private int x;
    private int y;

    /**
     * Instantiates a new {@link Resolution}.
     *
     * @param x the X
     * @param y the Y
     */
    public Resolution(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
