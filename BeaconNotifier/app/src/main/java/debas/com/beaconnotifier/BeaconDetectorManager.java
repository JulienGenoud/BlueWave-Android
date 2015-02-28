package debas.com.beaconnotifier;

import android.os.AsyncTask;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import debas.com.beaconnotifier.model.BeaconItemDB;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconDetectorManager {
    private static HashMap<BeaconItemSeen, Long> mBeaconAround = new HashMap<>();
    public final static long DEFAULT_TIME_OUT_LEAVE = 5000l, NO_TIMEOUT = -1;
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

    private Select searchBeaconFrom(Class classType, Beacon beacon) {
        return Select.from(classType)
                .where(Condition.prop("m_uuid").eq(beacon.getId1().toString()),
                        Condition.prop("m_major").eq(beacon.getId2().toInt()),
                        Condition.prop("m_minor").eq(beacon.getId3().toInt()));
    }

//    public void getCurrentBeaconsAround(Collection<Beacon> beacons, long currentTime, OnFinish onFinish) {
//        getCurrentBeaconsAround(beacons, currentTime, mDefaultTimeOutLeave, onFinish);
//    }

//    public synchronized void getCurrentBeaconsAround(Collection<Beacon> beacons, long currentTime, long timeOut, OnFinish onFinish) {
//        List<BeaconItemSeen> around = new ArrayList<>();
//        List<Beacon> newBeacons = new ArrayList<>();
//        int newBeacon = 0, lostBeacon = 0;
//
//        Log.d("around", "" + beacons.size());
//
//        /* remove timeout beacons */
//        for (Iterator<Map.Entry<BeaconItemSeen, Long>> it = mBeaconAround.entrySet().iterator(); it.hasNext();) {
//            Map.Entry<BeaconItemSeen, Long> entry = it.next();
//            long lastTimeSeen = entry.getValue();
//
//            Log.d("time", "" + (currentTime - lastTimeSeen));
//            if (currentTime - lastTimeSeen > timeOut) {
//                if (timeOut > 0) {
//                    it.remove();
//                    lostBeacon++;
//                } else {
//                    for (Beacon beacon : beacons) {
//                        if (entry.getKey().compare(beacon)) {
//                            it.remove();
//                            lostBeacon++;
//                        }
//                    }
//                }
//            } else {
//                around.add(entry.getKey());
//            }
//        }
//
//        for (Beacon beacon : beacons) {
//
//            Iterator exist = null;
//            for (Iterator<Map.Entry<BeaconItemSeen, Long>> it = mBeaconAround.entrySet().iterator(); it.hasNext(); ) {
//                Map.Entry<BeaconItemSeen, Long> entry = it.next();
//                BeaconItemSeen beaconItemSeen = entry.getKey();
//
//                /* look if yet detected so we update distance and time seen */
//                if (beaconItemSeen.compare(beacon)) {
//                    beaconItemSeen.mDistance = beacon.getDistance();
//                    entry.setValue(currentTime);
//                    exist = it;
//                }
//            }
//
//            if (exist == null) {
//                newBeacons.add(beacon);
//            }
//        }
//
//        /* if doesn't yet around search in db */
//        for (Beacon beacon : newBeacons) {
//
//            Log.d("test", "test1");
//            Select select;
//            BeaconItemSeen beaconItemSeen = null;
//
//            if ((select = searchBeaconFrom(BeaconItemSeen.class, beacon)).count() > 0) {
//                Log.d("test", "test2");
//                beaconItemSeen = (BeaconItemSeen) select.first();
//            } else if ((select = searchBeaconFrom(BeaconItemDB.class, beacon)).count() > 0) {
//                Log.d("test", "test3");
//                BeaconItemDB beaconItemDB = (BeaconItemDB) select.first();
//                beaconItemSeen = new BeaconItemSeen(beaconItemDB);
//            }
//
//            if (beaconItemSeen != null) {
//                Log.d("test", "test4");
//                mBeaconAround.put(beaconItemSeen, currentTime);
//                beaconItemSeen.mDistance = beacon.getDistance();
//                beaconItemSeen.mSeen = currentTime;
//                beaconItemSeen.save();
//                around.add(beaconItemSeen);
//                newBeacon++;
//            }
//        }
//        onFinish.result(around, newBeacon, lostBeacon);
//    }

    private List<BeaconItemSeen> mBeaconItemSeens = new ArrayList<>();

    public synchronized void getCurrentBeaconsAround(final Collection<Beacon> beacons, final long currentTime, final OnFinish onFinish) {
        final List<BeaconItemSeen> currentBeacon = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                for (Beacon beacon: beacons) {

                    BeaconItemSeen foundBeacon = null;

                    for (BeaconItemSeen beaconItemSeen : mBeaconItemSeens) {
                        if (beaconItemSeen.compare(beacon)) {
                            foundBeacon = beaconItemSeen;
                            break;
                        }
                    }

                    if (foundBeacon == null) {
                        Select select;

                        if ((select = searchBeaconFrom(BeaconItemSeen.class, beacon)).count() > 0) {
                            foundBeacon = (BeaconItemSeen) select.first();
                        } else if ((select = searchBeaconFrom(BeaconItemDB.class, beacon)).count() > 0) {
                            BeaconItemDB beaconItemDB = (BeaconItemDB) select.first();
                            foundBeacon = new BeaconItemSeen(beaconItemDB);
                        }
                    }

                    if (foundBeacon != null) {
                        mBeaconItemSeens.add(foundBeacon);
                        currentBeacon.add(foundBeacon);
                        foundBeacon.mDistance = beacon.getDistance();
                    }
                }
                onFinish.result(currentBeacon);
                return null;
            }
        }.execute();
    }

    public List<BeaconItemSeen> epurNewBeacons(List<BeaconItemSeen> oldBeacons, List<BeaconItemSeen> currentBeacons) {
        List<BeaconItemSeen> tmp = new ArrayList<>(currentBeacons);
        for (Iterator<BeaconItemSeen> b = currentBeacons.iterator(); b.hasNext(); ) {
            BeaconItemSeen beaconItemSeen = b.next();
            if (oldBeacons.contains(beaconItemSeen)) {
                b.remove();
            }
        }
        return tmp;
    }

    public interface OnFinish {
        public void result(List<BeaconItemSeen> beaconItemAround);
    }
}
