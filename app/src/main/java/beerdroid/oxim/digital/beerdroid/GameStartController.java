package beerdroid.oxim.digital.beerdroid;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class GameStartController {

    Gpio button1;
    Gpio button2;
    Gpio button3;
    Gpio button4;

    public GameStartController(final PeripheralManagerService peripheralService, final String bcm23, final String bcm24, final String bcm22, final String bcm27)
            throws IOException {
        button1 = setupGpio(peripheralService, bcm22);
        button2 = setupGpio(peripheralService, bcm23);
        button3 = setupGpio(peripheralService, bcm24);
        button4 = setupGpio(peripheralService, bcm27);
    }

    private Gpio setupGpio(final PeripheralManagerService peripheralManagerService,
                           final String gpioName) throws IOException {
        final Gpio gpio = peripheralManagerService.openGpio(gpioName);
        gpio.setDirection(Gpio.DIRECTION_IN);
        return gpio;
    }

    public void registerCallback(final GpioCallback gpioCallback) {
        try {
            button1.registerGpioCallback(gpioCallback);
            button2.registerGpioCallback(gpioCallback);
            button3.registerGpioCallback(gpioCallback);
            button4.registerGpioCallback(gpioCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unregisterCallback(final GpioCallback gpioCallback) {

        button1.unregisterGpioCallback(gpioCallback);
        button2.unregisterGpioCallback(gpioCallback);
        button3.unregisterGpioCallback(gpioCallback);
        button4.unregisterGpioCallback(gpioCallback);
    }
}
