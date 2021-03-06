package beerdroid.oxim.digital.beerdroid;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public final class PlayerController {

    private static final float MOVE_DIFF = 6.f;

    private final Gpio leftButton;
    private final Gpio rightButton;

    public PlayerController(final PeripheralManagerService peripheralManagerService,
                            final String leftButtonGpio,
                            final String rightButtonGpio) throws IOException {
        leftButton = setupGpio(peripheralManagerService, leftButtonGpio);
        rightButton = setupGpio(peripheralManagerService, rightButtonGpio);
    }

    private Gpio setupGpio(final PeripheralManagerService peripheralManagerService,
                           final String gpioName) throws IOException {
        final Gpio gpio = peripheralManagerService.openGpio(gpioName);
        gpio.setDirection(Gpio.DIRECTION_IN);
        return gpio;
    }

    public float consumeMovementDiff() {
        try {
            return getMovement();
        } catch (IOException e) {
            e.printStackTrace();
            return 0.f;
        }
    }

    private float getMovement() throws IOException {
        if (leftButton.getValue()) {
            return MOVE_DIFF;
        } else if (rightButton.getValue()) {
            return -MOVE_DIFF;
        } else {
            return 0.f;
        }
    }
}
