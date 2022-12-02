package tech.anapad.modela.hapticsboard.lra.location;

import tech.anapad.modela.hapticsboard.lra.LRA;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * {@link Location} is an immutable model representing an {@link LRA} location.
 */
public class Location {

    /**
     * Creates a new {@link Location} from the given <code>x</code> and <code>y</code>.
     *
     * @return the {@link Location}
     */
    public static Location loc(double x, double y) {
        return new Location(x, y);
    }

    private double x;
    private double y;

    /**
     * Instantiates a new {@link Location}.
     *
     * @param x the X
     * @param y the Y
     */
    private Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the distance of this {@link Location} from the given {@link Location}.
     *
     * @param location the {@link Location}
     *
     * @return the distance
     */
    public double distance(Location location) {
        return sqrt(pow(location.getX() - x, 2) + pow(location.getY() - y, 2));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
