package beerdroid.oxim.digital.beerdroid;

public final class Vector2D {

    public final double x;
    public final double y;

    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(final double x, final double y) {
        return new Vector2D(this.x + x, this.y + y);
    }

    public Vector2D add(final Vector2D vector2D) {
        return add(vector2D.x, vector2D.y);
    }

    public Vector2D subtract(final Vector2D vector2D) {
        return subtract(vector2D.x, vector2D.y);
    }

    public Vector2D subtract(final double x, final double y) {
        return new Vector2D(this.x - x, this.y - y);
    }

    public Vector2D normalize() {
        final double length = getLength();
        return new Vector2D(x / length, y / length);
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D multiplyBy(final double factor) {
        return new Vector2D(x * factor, y * factor);
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
