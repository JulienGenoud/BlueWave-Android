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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import debas.com.beaconnotifier.display.MainActivity;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.service.DailyListener;
import debas.com.beaconnotifier.utils.Constants;

/**
 * Created by debas on 13/10/14.
 */
public class BeaconNotifierApp extends SugarApp implements BootstrapNotifier, RangeNotifier {

    private BeaconManager mBeaconManager;
    private boolean createNotif = false;
    private List<BeaconItemSeen> beaconArround = new ArrayList<>();
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

        mBeaconManager.setBackgroundScanPeriod(1000l);
        mBeaconManager.setForegroundScanPeriod(1000l);

        /* !! should be set by the user in ui !! */
        mBeaconManager.setBackgroundBetweenScanPeriod(4000l);
        mBeaconManager.setForegroundBetweenScanPeriod(1000l);

        WakefulIntentService.scheduleAlarms(new DailyListener(), this, false);
    }

    @Override
    public void didEnterRegion(Region region) {

        if (createNotif) {
        }
    }

    public void setCreateNotif(boolean bool) {
        this.createNotif = bool;
        if (createNotif) {
            BeaconManager.getInstanceForApplication(this).setRangeNotifier(this);
        } else {
            oldBeacon = null;
        }
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    private List<BeaconItemSeen> oldBeacon = null;

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
        final BeaconDetectorManager beaconDetectorManager = BeaconDetectorManager.getInstance();
        beaconDetectorManager.getCurrentBeaconsAround(beacons, Calendar.getInstance().getTimeInMillis(), new BeaconDetectorManager.OnFinish() {
            @Override
            public void result(List<BeaconItemSeen> beaconItemAround) {

                if (oldBeacon == null) {
                    oldBeacon = new ArrayList<>(beaconItemAround);
                }

                oldBeacon = beaconDetectorManager.epurNewBeacons(oldBeacon, beaconItemAround);
                Log.d("background", "new beacons " + beaconItemAround.size());
                for (BeaconItemSeen beaconItemSeen : beaconItemAround) {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(BeaconNotifierApp.this)
                                    .setSmallIcon(R.drawable.consumer_beacon)
                                    .setContentTitle(getString(R.string.app_name))
                                    .setContentText(beaconItemSeen.mNotification)
                                    .setAutoCancel(true);

                    Intent targetIntent = new Intent(BeaconNotifierApp.this, MainActivity.class);
                    targetIntent.putExtra("FROM_NOTIFICATION", true);
                    targetIntent.putExtra("TEST", "Guillaume fillon suce des chibres");

                    PendingIntent contentIntent = PendingIntent.getActivity(BeaconNotifierApp.this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notifiy = builder.build();
                    notifiy.defaults |= Notification.DEFAULT_VIBRATE;
                    notifiy.defaults |= Notification.DEFAULT_SOUND;

                    beaconItemSeen.mSeen = Calendar.getInstance().getTimeInMillis();
                    beaconItemSeen.save();

                    nManager.notify(NOTIFICATION_ID, notifiy);
                }
            }
        });
    }
}
