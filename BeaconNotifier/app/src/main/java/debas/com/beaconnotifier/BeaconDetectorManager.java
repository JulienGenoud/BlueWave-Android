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
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconDetectorManager {
    private HashMap<BeaconItemSeen, Long> mBeaconAround = new HashMap<>();
    public final static long DEFAULT_TIME_OUT_LEAVE = 5000l;
    private final long mDefaultTimeOutLeave;

    public BeaconDetectorManager() {
        this(DEFAULT_TIME_OUT_LEAVE);
    }

    public BeaconDetectorManager(long timeOutLeave) {
        mDefaultTimeOutLeave = timeOutLeave;
    }

    public List<BeaconItemSeen> getCurrentBeaconsAround(Collection<Beacon> beacons, long currentTime) {
        List<BeaconItemSeen> around = new ArrayList<>();
        List<Beacon> newBeacon = new ArrayList<>();

        for(Iterator<Map.Entry<BeaconItemSeen, Long>> it = mBeaconAround.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BeaconItemSeen, Long> entry = it.next();

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
            for(Map.Entry<BeaconItemSeen, Long> entry : mBeaconAround.entrySet()) {
                BeaconItemSeen beaconItemDB = entry.getKey();

                /* look if yet detected */
                if (beaconItemDB.compare(beacon)) {
                    beaconItemDB.mDistance =  beacon.getDistance();
                    around.add(beaconItemDB);
                    exist = true;
                }
            }

            if (!exist) {
                newBeacon.add(beacon);
            }
        }

        /* if doesn't yet around search in db */
        for (Beacon beacon : newBeacon) {

            Select select;
            BeaconItemSeen beaconItemSeen = null;

            if ((select = searchBeaconFrom(BeaconItemSeen.class, beacon)).count() > 0) {
                beaconItemSeen = (BeaconItemSeen) select.first();

                Log.d("query", "from seen");
            } else if ((select = searchBeaconFrom(BeaconItemDB.class, beacon)).count() > 0) {
                BeaconItemDB beaconItemDB = (BeaconItemDB) select.first();

                beaconItemSeen = new BeaconItemSeen(beaconItemDB);
                beaconItemSeen.save();
                Log.d("query", "from global");
            }

            if (beaconItemSeen != null) {
                mBeaconAround.put(beaconItemSeen, currentTime);
                beaconItemSeen.mDistance = beacon.getDistance();
                around.add(beaconItemSeen);
            }
        }
        return around;
    }

    private Select searchBeaconFrom(Class classType, Beacon beacon) {
        return Select.from(classType)
                .where(Condition.prop("m_uuid").eq(beacon.getId1().toString()),
                        Condition.prop("m_major").eq(beacon.getId2().toInt()),
                        Condition.prop("m_minor").eq(beacon.getId3().toInt()));
    }
}
