package debas.com.beaconnotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.orm.SugarApp;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

import debas.com.beaconnotifier.display.MainActivity;
import debas.com.beaconnotifier.service.DailyListener;
import debas.com.beaconnotifier.utils.Constants;

/**
 * Created by debas on 13/10/14.
 */
public class BeaconNotifierApp extends SugarApp implements BootstrapNotifier, RangeNotifier {

    private BeaconManager mBeaconManager;
    private boolean createNotif = false;

    public static final int NOTIFICATION_ID = 12345;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("BeaconNotifierApp", "created");

        /* add header beacon gimbal */
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(Constants.GIMBAL_HEADER));

        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);

        /* search only my beacon for debuging */
        Region region = new Region("RegionBootstrap", null, null, null);

        RegionBootstrap regionBootstrap = new RegionBootstrap(this, region);

        mBeaconManager.setBackgroundBetweenScanPeriod(5000l);

        WakefulIntentService.scheduleAlarms(new DailyListener(), this, false);
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

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

    }
}
