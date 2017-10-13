package beerdroid.oxim.digital.beerdroid;

import android.graphics.Rect;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

public final class GameManager {

    public enum GameStatus {
        IDLE,
        PAUSED,
        PLAYING,
        PLAYER_1_WINS,
        PLAYER_2_WINS,
        RESTART
    }

    private static final int BLANK_AREA = 100;

    private final GameClock gameClock = new GameClock();

    private Subscription gameClockSubscription = Subscriptions.empty();
    private PublishSubject<GameStatus> gameStatusObservable = PublishSubject.create();

    private BallController ballController = new BallController();
    private PlayerController leftPlayerController = GameControllerFactory.createPlayerOneController();
    private PlayerController rightPlayerController = GameControllerFactory.createPlayerTwoController();

    private Rect gameAreaRect;

    private Paddle leftPlayer;
    private Paddle rightPlayer;

    private RegularBall ball;

    public void setup(final Rect screenSize, final Paddle leftPlayer, final Paddle rightPlayer, final RegularBall ball) {
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
        this.ball = ball;
        init(screenSize);
        initGameStartController();
    }

    final GpioCallback callback = new GpioCallback() {

        @Override
        public boolean onGpioEdge(final Gpio gpio) {
            if (gameStopped) {
                gameStatusObservable.onNext(GameStatus.RESTART);
            }
            return super.onGpioEdge(gpio);
        }
    };

    private void initGameStartController() {
        leftPlayerController.unregisterCallback(callback);
        rightPlayerController.unregisterCallback(callback);

        leftPlayerController.setFirstKeyCallback(callback);
        rightPlayerController.setFirstKeyCallback(callback);
    }

    private boolean gameStopped = false;

    public void stopGame() {
        gameClockSubscription.unsubscribe();
    }

    public Observable<GameStatus> gameStatusObservable() {
        return gameStatusObservable;
    }

    private void init(final Rect gameBoardSize) {
        this.gameAreaRect = gameBoardSize;
        this.ball.translate(new Vector2D(gameAreaRect.centerX(), gameAreaRect.centerY()));
        ballController.reset();
    }

    public void startGame() {
        gameStopped = false;
        gameClockSubscription = gameClock.gameClockObservable()
                                         .onBackpressureDrop()
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(this::onGameClockTick);
        gameStatusObservable.onNext(GameStatus.PLAYING);
    }

    private void onGameClockTick(final long tick) {
        movePlayer(leftPlayer, leftPlayerController);
        movePlayer(rightPlayer, rightPlayerController);

        moveBall(ball, ballController);

        //checkForGameEnd();
    }

    private void moveBall(final RegularBall ball, final BallController ballController) {
        final Vector2D direction = ballController.getDirection();
        final Vector2D translation = ballController.getTranslationVector();
        final Vector2D currentPoint = ball.getPosition();

        ball.translate(currentPoint.add(translation));

        checkForGameEnd();
        checkForCollision(direction);
    }

    private void checkForCollision(final Vector2D directionVector) {
        if (ball.getTop() <= gameAreaRect.top) {
            Log.e("Game", "Top collision");
            ballController.setDirection(new Vector2D(directionVector.x, -directionVector.y));
        } else if (ball.getBottom() >= gameAreaRect.bottom) {
            Log.e("Game", "Bottom collision");
            ballController.setDirection(new Vector2D(directionVector.x, -directionVector.y));
        }

        if (collidedWithLeftPaddle(ball, leftPlayer)) {
            Log.e("Game", "Left player collision");
            ballController.speedUpBy(1f);

            final Vector2D reflectedDirection = calculateReflectedDirection(ball, leftPlayer, directionVector);
            ballController.setDirection(reflectedDirection);
        } else if (collidedWithRightPaddle(ball, rightPlayer)) {
            Log.e("Game", "Right player collision");
            ballController.speedUpBy(1f);

            final Vector2D reflectedDirection = calculateReflectedDirection(ball, rightPlayer, directionVector);
            ballController.setDirection(reflectedDirection);
        }
    }

    private Vector2D calculateReflectedDirection(final RegularBall ball, final Paddle paddle, final Vector2D directionVector) {
        final float distanceFromMiddle = paddle.getBottom() - ball.getBottom() - paddle.getHeight() / 2;
        final float collisionPercentage = distanceFromMiddle / (paddle.getHeight() / 2) * -0.8f;

        return new Vector2D(-directionVector.x, collisionPercentage);
    }

    private boolean collidedWithRightPaddle(final RegularBall ball, final Paddle paddle) {
        return ball.getRight() >= paddle.getLeft() && ball.getTop() <= paddle.getBottom() && ball.getBottom() >= paddle.getTop();
    }

    private boolean collidedWithLeftPaddle(final RegularBall ball, final Paddle paddle) {
        return ball.getLeft() <= paddle.getRight() && ball.getTop() <= paddle.getBottom() && ball.getBottom() >= paddle.getTop();
    }

    private void checkForGameEnd() {
        final float startBorderX = gameAreaRect.left;
        final float endBorderX = gameAreaRect.right;

        if (ball.getLeft() > endBorderX) {
            Log.e("Game", "Over in the right");
            gameStatusObservable.onNext(GameStatus.PLAYER_1_WINS);
            gameStopped = true;
            //stopGame();
        } else if (ball.getRight() < startBorderX) {
            gameStatusObservable.onNext(GameStatus.PLAYER_2_WINS);
            gameStopped = true;
            Log.e("Game", "Over in the left");
            // stopGame();
        }
    }

    private void movePlayer(final Paddle player, final PlayerController playerController) {
        final float playerTop = player.getTop();
        final float playerBottom = player.getBottom();
        final float requestedMovementDiff = playerController.consumeMovementDiff();

        final float actualMovement;
        if (requestedMovementDiff > 0) {
            final float remainingAllowedBottomMovement = gameAreaRect.bottom - playerBottom;
            actualMovement = Math.min(remainingAllowedBottomMovement, requestedMovementDiff);
        } else if (requestedMovementDiff < 0) {
            final float remainingAllowedTopMovement = playerTop - gameAreaRect.top;
            actualMovement = Math.max(remainingAllowedTopMovement * -1, requestedMovementDiff);
        } else {
            actualMovement = 0;
        }
        if (actualMovement != 0) {
            player.translate(playerTop + actualMovement);
        }
    }
}
