package debas.com.beaconnotifier.service;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Created by debas on 15/10/14.
 */
public class PullBeaconService extends WakefulIntentService {

    public PullBeaconService() {
        super("PullBeaconService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.d("PullBeaconService", "doo stuff");
    }
}
