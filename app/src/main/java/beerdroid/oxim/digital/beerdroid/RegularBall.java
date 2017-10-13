package beerdroid.oxim.digital.beerdroid;

import android.view.View;

public final class RegularBall implements GameObject {

    private final View ballView;

    public RegularBall(final View ballView) {
        this.ballView = ballView;
    }

    public float getTop() {
        return ballView.getY();
    }

    public float getBottom() {
        return getTop() + ballView.getHeight();
    }

    public float getLeft() {
        return ballView.getX();
    }

    public float getRight() {
        return getLeft() + ballView.getWidth();
    }

    public void translate(final Vector2D newPoint) {
        ballView.setX((float) newPoint.x);
        ballView.setY((float) newPoint.y);
    }

    public Vector2D getPosition() {
        return new Vector2D(ballView.getX(), ballView.getY());
    }

    @Override
    public String toString() {
        return String.format("Ball (%f,%f,%f,%f)", getLeft(), getTop(), getRight(), getBottom());
    }
}
