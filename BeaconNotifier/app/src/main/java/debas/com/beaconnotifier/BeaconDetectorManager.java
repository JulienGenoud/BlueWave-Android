package debas.com.beaconnotifier;

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
    private static HashMap<BeaconItemSeen, Long> mBeaconAround = new HashMap<>();
    public final static long DEFAULT_TIME_OUT_LEAVE = 5000l;
    private static long mDefaultTimeOutLeave;
    private static BeaconDetectorManager mBeaconDetectorManager = null;

    public static BeaconDetectorManager getInstance() {
        if (mBeaconDetectorManager == null) {
            mBeaconDetectorManager = new BeaconDetectorManager();
        }
        return mBeaconDetectorManager;
    }

    private BeaconDetectorManager() {
        mDefaultTimeOutLeave = DEFAULT_TIME_OUT_LEAVE;
    }

    public void setTimeOutLeave(long timeOut) {
        mDefaultTimeOutLeave = timeOut;
    }

    private Select searchBeaconFrom(Class classType, Beacon beacon) {
        return Select.from(classType)
                .where(Condition.prop("m_uuid").eq(beacon.getId1().toString()),
                        Condition.prop("m_major").eq(beacon.getId2().toInt()),
                        Condition.prop("m_minor").eq(beacon.getId3().toInt()));
    }

    public synchronized void getCurrentBeaconsAround(Collection<Beacon> beacons, long currentTime, OnFinish onFinish) {
        List<BeaconItemSeen> around = new ArrayList<>();
        List<Beacon> newBeacons = new ArrayList<>();
        int newBeacon = 0, lostBeacon = 0;

        /* remove timeout beacons */
        for (Iterator<Map.Entry<BeaconItemSeen, Long>> it = mBeaconAround.entrySet().iterator(); it.hasNext();) {
            Map.Entry<BeaconItemSeen, Long> entry = it.next();
            long lastTimeSeen = entry.getValue();

            if (currentTime - lastTimeSeen > mDefaultTimeOutLeave) {
                it.remove();
                lostBeacon++;
            } else {
                around.add(entry.getKey());
            }
        }

        for (Beacon beacon : beacons) {

            boolean exist = false;
            for (Map.Entry<BeaconItemSeen, Long> entry :  mBeaconAround.entrySet()) {
                BeaconItemSeen beaconItemSeen = entry.getKey();

                /* look if yet detected so we update distance and time seen */
                if (beaconItemSeen.compare(beacon)) {
                    beaconItemSeen.mDistance = beacon.getDistance();
                    entry.setValue(currentTime);
                    exist = true;
                }
            }

            if (!exist) {
                newBeacons.add(beacon);
            }
        }

        /* if doesn't yet around search in db */
        for (Beacon beacon : newBeacons) {

            Select select;
            BeaconItemSeen beaconItemSeen = null;

            if ((select = searchBeaconFrom(BeaconItemSeen.class, beacon)).count() > 0) {
                beaconItemSeen = (BeaconItemSeen) select.first();
            } else if ((select = searchBeaconFrom(BeaconItemDB.class, beacon)).count() > 0) {
                BeaconItemDB beaconItemDB = (BeaconItemDB) select.first();
                beaconItemSeen = new BeaconItemSeen(beaconItemDB);
            }

            if (beaconItemSeen != null) {
                mBeaconAround.put(beaconItemSeen, currentTime);
                beaconItemSeen.mDistance = beacon.getDistance();
                beaconItemSeen.mSeen = currentTime;
                beaconItemSeen.save();
                around.add(beaconItemSeen);
                newBeacon++;
            }
        }
        onFinish.result(around, newBeacon, lostBeacon);
    }

    public interface OnFinish {
        public void result(List<BeaconItemSeen> beaconItemAround, int newBeacons, int lostBeacon);
    }
}
