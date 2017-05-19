package beerdroid.oxim.digital.beerdroid;

import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public final class PlayerControllerFactory {

    private static final PeripheralManagerService peripheralService = new PeripheralManagerService();

    public static PlayerController createPlayerOneController() {
        try {
            return new PlayerController(peripheralService, GPIOS.BCM22, GPIOS.BCM27);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerController createPlayerTwoController() {
        try {
            return new PlayerController(peripheralService, GPIOS.BCM23, GPIOS.BCM24);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
