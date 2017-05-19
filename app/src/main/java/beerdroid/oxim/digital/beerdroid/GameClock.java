package beerdroid.oxim.digital.beerdroid;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public final class GameClock {

    public GameClock() {
    }

    public Observable<Long> gameClockObervable() {
        return Observable.interval(16, TimeUnit.MILLISECONDS);
    }
}
