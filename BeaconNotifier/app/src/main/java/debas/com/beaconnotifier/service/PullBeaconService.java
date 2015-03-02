package debas.com.beaconnotifier.service;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import debas.com.beaconnotifier.AsyncTaskDB;
import debas.com.beaconnotifier.database.BeaconDataBase;

/**
 * Created by debas on 15/10/14.
 */
public class PullBeaconService extends WakefulIntentService {

    public PullBeaconService() {
        super("PullBeaconService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.d("PullBeaconService", "updating");

        BeaconDataBase mBeaconDataBase = BeaconDataBase.getInstance(getApplicationContext());
        mBeaconDataBase.updateDB(getApplicationContext(), new AsyncTaskDB.OnDBUpdated() {
            @Override
            public void onDBUpdated(boolean result, int nbElement) {
                if (!result) {
                    Log.d("PullBeaconService", "failed to update DB");
                } else {
                    Log.d("PullBeaconService", "success to update DB : new element " + nbElement);
                }
            }
        });
    }
}
