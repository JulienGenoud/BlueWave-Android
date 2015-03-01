package debas.com.beaconnotifier.display;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 28/02/15.
 */
public class BeaconActivity extends ActionBarActivity {

    public static String BEACON_UUID = "beacon_uuid";
    public static String BEACON_MAJOR = "beacon_major";
    public static String BEACON_MINOR = "beacon_minor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beacon_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("TEST")) {
                String info = bundle.getString("TEST");
                ((TextView) findViewById(R.id.text_view)).setText(info);
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
