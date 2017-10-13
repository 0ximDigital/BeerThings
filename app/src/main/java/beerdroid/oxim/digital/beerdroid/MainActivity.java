package beerdroid.oxim.digital.beerdroid;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public final class MainActivity extends AppCompatActivity {

    private GameManager gameManager = new GameManager();

    private Subscription gameStatusSubscription = Subscriptions.empty();

    View playerOneView;
    View playerTwoView;
    View ballView;

    TextView statusText;

    TextView player1Score;
    TextView player2Score;

    private int player1ScoreCounter = 0;
    private int player2ScoreCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerOneView = findViewById(R.id.player_one);
        playerTwoView = findViewById(R.id.player_two);
        ballView = findViewById(R.id.ball);

        player1Score = (TextView) findViewById(R.id.player_one_score);
        player2Score = (TextView) findViewById(R.id.player_two_score);
        statusText = (TextView) findViewById(R.id.status_text);

        ballView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                ballView.getViewTreeObserver().removeOnPreDrawListener(this);
                startGame();
                return false;
            }
        });
    }

    private void startGame() {
        if (gameStatusSubscription != null && !gameStatusSubscription.isUnsubscribed()) {
            gameStatusSubscription.unsubscribe();
        }

        statusText.setVisibility(View.INVISIBLE);

        final int height = getWindow().getDecorView().getHeight();
        final int width = getWindow().getDecorView().getWidth();

        Resources r = getResources();
        int px50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        int px20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

        final Rect screenSize = new Rect(px20, px50, width - px20, height - px50);

        gameManager.setup(screenSize, new Paddle(playerOneView), new Paddle(playerTwoView), new RegularBall(ballView));
        gameManager.startGame();

        gameStatusSubscription = gameManager.gameStatusObservable()
                                            .subscribe(this::onGameStatus, Throwable::printStackTrace);
    }

    private void onGameStatus(final GameManager.GameStatus gameStatus) {
        if (gameStatus == GameManager.GameStatus.PLAYER_1_WINS) {
            gameEnded(gameStatus);
        } else if (gameStatus == GameManager.GameStatus.PLAYER_2_WINS) {
            gameEnded(gameStatus);
        } else if (gameStatus == GameManager.GameStatus.RESTART) {
            restartGame();
        }
    }

    private void gameEnded(GameManager.GameStatus gameStatus) {
        gameManager.stopGame();

        final boolean playerOneWon = gameStatus == GameManager.GameStatus.PLAYER_1_WINS;
        statusText.setVisibility(View.VISIBLE);
        increaseScore(gameStatus);
        if (playerOneWon) {
            statusText.setText("Player one won");
        } else {
            statusText.setText("Player two won");
        }
    }

    private void increaseScore(final GameManager.GameStatus gameStatus) {
        if (gameStatus == GameManager.GameStatus.PLAYER_1_WINS) {
            player1ScoreCounter++;
        } else if (gameStatus == GameManager.GameStatus.PLAYER_2_WINS) {
            player2ScoreCounter++;
        }
        player1Score.setText(player1ScoreCounter + "");
        player2Score.setText(player2ScoreCounter + "");
    }

    private void restartGame() {
        startGame();
    }

    //on start game click, hide view and start game

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameStatusSubscription.unsubscribe();
    }
}
