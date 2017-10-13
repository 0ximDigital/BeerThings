package beerdroid.oxim.digital.beerdroid;

import java.util.Random;

public final class BallController {

    private static final float INITIAL_SPEED = 10.0f;
    private static final float INITIAL_X = 0.33f;
    private static final float INITIAL_Y = 0.66f;

    final float maxY = -0.5f;
    final float minY = 0.5f;

    final float maxX = -0.9f;
    final float minX = 0.9f;

    private Vector2D direction;
    private double speedMultiplier;

    private final Random ballDirectionGenerator = new Random();

    public BallController() {
        init();
    }

    public void init() {

        final float y = ballDirectionGenerator.nextFloat() * (maxY - minY) + minY;
        final float x = ballDirectionGenerator.nextFloat() * (maxX - minX) + minX;

        direction = new Vector2D(x, y).normalize();
        speedMultiplier = INITIAL_SPEED;
    }

    public void setDirection(final Vector2D newDirection) {
        this.direction = newDirection.normalize();
    }

    public Vector2D getDirection() {
        return direction;
    }

    public Vector2D getTranslationVector() {
        return direction.multiplyBy(speedMultiplier);
    }

    public void speedUpBy(final double increaseFactor) {
        this.speedMultiplier = speedMultiplier + increaseFactor;
    }

    public void reset() {
        init();
    }
}
