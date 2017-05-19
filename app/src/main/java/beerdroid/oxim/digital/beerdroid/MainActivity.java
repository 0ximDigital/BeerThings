package beerdroid.oxim.digital.beerdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;

public class MainActivity extends AppCompatActivity {

    private PeripheralManagerService peripheralService = new PeripheralManagerService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("PMS", "Available GPIOs: " + peripheralService.getGpioList());
    }
}
