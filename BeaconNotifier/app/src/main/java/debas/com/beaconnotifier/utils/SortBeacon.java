package debas.com.beaconnotifier.utils;

import java.util.Comparator;

import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 13/10/14.
 */
public class SortBeacon implements Comparator<BeaconItemSeen> {

    @Override
    public int compare(BeaconItemSeen beacon1, BeaconItemSeen beacon2) {
        if (beacon1.mDistance > beacon2.mDistance) {
            return 1;
        } else if (beacon1.mDistance < beacon2.mDistance) {
            return -1;
        } else {
            return 0;
        }
    }
}
