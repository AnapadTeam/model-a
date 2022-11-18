package tech.anapad.modela.touchscreen.model;

/**
 * {@link Touch} is a model representing a touchscreen touch. Instantiate objects of this class should be treated as
 * immutable.
 */
public class Touch {

    private int id;
    private int x;
    private int y;
    private int size;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
