package debas.com.beaconnotifier.utils;

import org.altbeacon.beacon.Beacon;

import java.util.Comparator;

/**
 * Created by debas on 13/10/14.
 */
public class SortBeacon implements Comparator<Beacon> {

    @Override
    public int compare(Beacon beacon1, Beacon beacon2) {
        if (beacon1.getDistance() > beacon2.getDistance()) {
            return 1;
        } else if (beacon1.getDistance() < beacon2.getDistance()) {
            return -1;
        } else {
            return 0;
        }
    }
}
