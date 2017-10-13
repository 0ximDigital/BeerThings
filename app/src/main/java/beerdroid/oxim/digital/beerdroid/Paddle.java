package beerdroid.oxim.digital.beerdroid;

import android.view.View;

public final class Paddle implements GameObject {

    private final View paddleView;

    public Paddle(final View paddleView) {
        this.paddleView = paddleView;
    }

    public void translate(final float translationY) {
        paddleView.setY(translationY);
    }

    public float getTop() {
        return paddleView.getY();
    }

    public float getBottom() {
        return paddleView.getY() + paddleView.getHeight();
    }

    public float getLeft() {
        return paddleView.getX();
    }

    public float getRight() {
        return getLeft() + paddleView.getWidth();
    }

    public float getHeight() {
        return paddleView.getHeight();
    }

    @Override
    public String toString() {
        return String.format("Paddle (%f,%f,%f,%f)", getLeft(), getTop(), getRight(), getBottom());
    }
}
