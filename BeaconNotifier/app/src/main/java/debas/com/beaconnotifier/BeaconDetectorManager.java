package debas.com.beaconnotifier;

import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import debas.com.beaconnotifier.model.BeaconItemDB;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconDetectorManager {
    private HashMap<BeaconItemDB, Long> mBeaconAround = new HashMap<>();
    public final static long DEFAULT_TIME_OUT_LEAVE = 5000l;
    private final long mDefaultTimeOutLeave;

    public BeaconDetectorManager() {
        this(DEFAULT_TIME_OUT_LEAVE);
    }


    public BeaconDetectorManager(long timeOutLeave) {
        mDefaultTimeOutLeave = timeOutLeave;
    }

    public List<BeaconItemDB> getCurrentBeaconsAround(Collection<Beacon> beacons, long currentTime) {
        List<BeaconItemDB> around = new ArrayList<>();
        List<Beacon> newBeacon = new ArrayList<>();

        for(Iterator<Map.Entry<BeaconItemDB, Long>> it = mBeaconAround.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BeaconItemDB, Long> entry = it.next();

            long lastTimeSeen = entry.getValue();
            if (currentTime - lastTimeSeen > mDefaultTimeOutLeave) {
                it.remove();
                Log.d("supress", (currentTime - lastTimeSeen) + "");
            } else {
                entry.setValue(currentTime);
            }
        }

        for (Beacon beacon : beacons) {

            boolean exist = false;
            for(Map.Entry<BeaconItemDB, Long> entry : mBeaconAround.entrySet()) {
                BeaconItemDB beaconItemDB = entry.getKey();

                /* look if yet detected */
                if (beaconItemDB.compare(beacon)) {
                    beaconItemDB.setDistance(beacon.getDistance());
                    around.add(beaconItemDB);
                    exist = true;
                }
            }

            if (!exist) {
                newBeacon.add(beacon);
            }
        }

        Log.d("newbacon", newBeacon.size() + " - " + mBeaconAround.size());

        /* if doesn't yet around search in db */
        for (Beacon beacon : newBeacon) {
            Select beaconSearch = Select.from(BeaconItemDB.class)
                    .where(Condition.prop("m_uuid").eq(beacon.getId1().toString()),
                            Condition.prop("m_major").eq(beacon.getId2().toInt()),
                            Condition.prop("m_minor").eq(beacon.getId3().toInt()));

            List<BeaconItemDB> beaconItemDBs = BeaconItemDB.listAll(BeaconItemDB.class);
            Log.d("numbeacon", "" + beaconItemDBs.size());

            for (BeaconItemDB beaconItemDB : beaconItemDBs) {
                Log.d("field", "+++++++++++++++++++++++++++++++++++");
                Log.d("field uuid", beacon.getId1().toString());
                Log.d("field major", beacon.getId2().toInt() + "");
                Log.d("field minor", beacon.getId3().toInt() + "");

                Log.d("field", "-----------------------------------");

                Log.d("field uuid", beaconItemDB.mUuid);
                Log.d("field major", beaconItemDB.mMajor + "");
                Log.d("field minor", beaconItemDB.mMinor + "");
                Log.d("field", "+++++++++++++++++++++++++++++++++++");
            }
            if (beaconSearch.count() > 0) {
                BeaconItemDB beaconItemDB = (BeaconItemDB) beaconSearch.first();
                mBeaconAround.put(beaconItemDB, currentTime);
                beaconItemDB.setDistance(beacon.getDistance());
                around.add(beaconItemDB);
            }
        }
        return around;
    }
}
