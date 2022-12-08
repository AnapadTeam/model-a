package tech.anapad.modela.touchscreen.driver;

import static java.lang.Double.compare;

/**
 * {@link Touch} is an immutable model representing a touchscreen touch.
 */
public class Touch implements Comparable<Touch> {

    private int id;
    private int x;
    private int y;
    private int size;

    /**
     * {@link Builder} is an object builder for {@link Touch}.
     */
    public static class Builder {

        private final Touch touch;

        /**
         * Instantiates a new {@link Builder}.
         */
        public Builder() {
            this.touch = new Touch();
        }

        public Builder id(int id) {
            touch.id = id;
            return this;
        }

        public Builder x(int x) {
            touch.x = x;
            return this;
        }

        public Builder y(int y) {
            touch.y = y;
            return this;
        }

        public Builder size(int size) {
            touch.size = size;
            return this;
        }

        public Touch build() {
            return touch;
        }
    }

    public int getID() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    @Override
    public int compareTo(Touch touch) {
        return compare(id, touch.id);
    }

    @Override
    public String toString() {
        return "Touch{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", size=" + size +
                '}';
    }
}
