package debas.com.beaconnotifier.display;

import android.app.Activity;
import android.os.Bundle;

import debas.com.beaconnotifier.R;

/**
 * Created by julien on 26/11/14.
 */
public class MyBeaconActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mybeacon);
    }
}
