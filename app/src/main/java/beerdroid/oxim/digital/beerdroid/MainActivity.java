package beerdroid.oxim.digital.beerdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

public class MainActivity extends AppCompatActivity {

    private PlayerController playerOneController;
    private PlayerController playerTwoController;

    private final GameClock gameClock = new GameClock();

    private Subscription gameClockSubscription = Subscriptions.empty();

    private View playerOneView;
    private View playerTwoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerOneController = PlayerControllerFactory.createPlayerOneController();
        playerTwoController = PlayerControllerFactory.createPlayerTwoController();

        playerOneView = findViewById(R.id.player_one);
        playerTwoView = findViewById(R.id.player_two);

        gameClockSubscription = gameClock.gameClockObervable()
                                         .onBackpressureDrop()
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(this::onGameClockTick);
    }

    private void onGameClockTick(final Long tick) {
        final float playerOneMovement = playerOneController.consumeMovementDiff();
        final float playerTwoMovement = playerTwoController.consumeMovementDiff();

        playerOneView.setTranslationY(playerOneView.getTranslationY() + playerOneMovement);
        playerTwoView.setTranslationY(playerTwoView.getTranslationY() + playerTwoMovement);
    }

    @Override
    protected void onDestroy() {
        gameClockSubscription.unsubscribe();
        super.onDestroy();
    }
}
