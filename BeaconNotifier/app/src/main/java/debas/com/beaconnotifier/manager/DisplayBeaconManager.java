package debas.com.beaconnotifier.manager;

import java.util.HashMap;
import java.util.List;

import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.database.BeaconItemDB;

/**
 * Created by debas on 25/11/14.
 */
public class DisplayBeaconManager {
    // Long represent last time in millis detection
    private HashMap<BeaconItemDB, Long> mBeaconAround = new HashMap<BeaconItemDB, Long>();
    private BeaconDataBase mBeaconDataBase = null;
    private long mTimeOutRemove;

    public DisplayBeaconManager(long timeOutRemove, BeaconDataBase beaconDataBase) {
        mTimeOutRemove = timeOutRemove;
        mBeaconDataBase = beaconDataBase;
    }

    public void updateBeaconAround(List<BeaconItemDB> allBeacon) {

    }
}
