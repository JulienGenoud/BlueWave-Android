package debas.com.beaconnotifier;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.display.BeaconViewer;
import debas.com.beaconnotifier.display.MainActivity;
import debas.com.beaconnotifier.service.DailyListener;
import debas.com.beaconnotifier.utils.Constants;

/**
 * Created by debas on 13/10/14.
 */
public class BeaconNotifierApp extends Application implements BootstrapNotifier {

    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private BeaconDataBase mBeaconDataBase = null;
    private boolean createNotif = false;

    public static final int NOTIFICATION_ID = 12345;

    @Override
    public void onCreate() {
        Log.d("BeaconNotifierApp", "created");

        /* set header beacon to gimbal */
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);

        /* search only my beacon for debuging */
        Region region = new Region("RegionBootstrap",
                Identifier.parse("53168465-4534-6543-2134-546865413213"),
                Identifier.fromInt(10),
                Identifier.fromInt(1));
        RegionBootstrap regionBootstrap = new RegionBootstrap(this, region);

        mBeaconManager.setBackgroundBetweenScanPeriod(5000l);

        WakefulIntentService.scheduleAlarms(new DailyListener(), this, false);

        /* create beacon database instance */
        mBeaconDataBase = new BeaconDataBase(getApplicationContext());

        /* check if this is the first time run */
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_LAUNCHED, true);
        if (firstTimeRun) {
            // update database
        }

        super.onCreate();
    }

    @Override
    public void didEnterRegion(Region region) {

        if (createNotif) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.consumer_beacon)
                            .setContentTitle("BeaconNotifier")
                            .setContentText("Something interesting happened")
                            .setAutoCancel(true);

            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notifiy = builder.build();
            notifiy.defaults |= Notification.DEFAULT_VIBRATE;
            notifiy.defaults |= Notification.DEFAULT_SOUND;

            nManager.notify(NOTIFICATION_ID, notifiy);
        }
    }

    public void setCreateNotif(boolean bool) {
        createNotif = bool;
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    public BeaconDataBase getBeaconDataBase() {
        return mBeaconDataBase;
    }
}
