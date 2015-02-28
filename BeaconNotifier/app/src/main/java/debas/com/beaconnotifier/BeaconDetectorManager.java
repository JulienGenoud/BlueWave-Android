package debas.com.beaconnotifier;

import android.os.AsyncTask;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import debas.com.beaconnotifier.model.BeaconItemDB;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconDetectorManager {
    private static BeaconDetectorManager mBeaconDetectorManager = null;

    public static BeaconDetectorManager getInstance() {
        if (mBeaconDetectorManager == null) {
            mBeaconDetectorManager = new BeaconDetectorManager();
        }
        return mBeaconDetectorManager;
    }

    private BeaconDetectorManager() {

    }

    private Select searchBeaconFrom(Class classType, Beacon beacon) {
        return Select.from(classType)
                .where(Condition.prop("m_uuid").eq(beacon.getId1().toString()),
                        Condition.prop("m_major").eq(beacon.getId2().toInt()),
                        Condition.prop("m_minor").eq(beacon.getId3().toInt()));
    }

    public synchronized void getCurrentBeaconsAround(final Collection<Beacon> beacons, final long currentTime, final OnFinish onFinish) {
        final List<BeaconItemSeen> currentBeacon = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                for (Beacon beacon: beacons) {

                    BeaconItemSeen foundBeacon = null;

                    Select select;

                    if ((select = searchBeaconFrom(BeaconItemSeen.class, beacon)).count() > 0) {
                        foundBeacon = (BeaconItemSeen) select.first();
                    } else if ((select = searchBeaconFrom(BeaconItemDB.class, beacon)).count() > 0) {
                        BeaconItemDB beaconItemDB = (BeaconItemDB) select.first();
                        foundBeacon = new BeaconItemSeen(beaconItemDB);
                    }

                    if (foundBeacon != null) {
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
