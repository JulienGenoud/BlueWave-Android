package debas.com.beaconnotifier.utils;

import java.util.Comparator;

import debas.com.beaconnotifier.model.BeaconItemDB;

/**
 * Created by debas on 13/10/14.
 */
public class SortBeacon implements Comparator<BeaconItemDB> {

    @Override
    public int compare(BeaconItemDB beacon1, BeaconItemDB beacon2) {
        if (beacon1.getDistance() > beacon2.getDistance()) {
            return 1;
        } else if (beacon1.getDistance() < beacon2.getDistance()) {
            return -1;
        } else {
            return 0;
        }
    }
}
