package debas.com.beaconnotifier.model;

import com.orm.dsl.Ignore;

import java.util.Random;

/**
 * Created by debas on 19/02/15.
 */
public class BeaconItemSeen extends BeaconItemDB {
    public boolean mFavorites = false;
    public long mSeen = 0;

    @Ignore
    public double mDistance = 0;

    public BeaconItemSeen() {

    }

    public BeaconItemSeen(BeaconItemDB beaconItemDB) {
        mBeaconId = beaconItemDB.mBeaconId;
        mUuid = beaconItemDB.mUuid;
        mMajor = beaconItemDB.mMajor;
        mMinor = beaconItemDB.mMinor;
        mNotification = beaconItemDB.mNotification;
        mRange = beaconItemDB.mRange;
    }

    static Random random = new Random();

    public static BeaconItemSeen generateRandom() {
        BeaconItemSeen beaconItemSeen = new BeaconItemSeen();

        beaconItemSeen.mBeaconId = "" + random.nextInt();
        beaconItemSeen.mUuid = "" + random.nextInt();
        beaconItemSeen.mMajor = random.nextInt();
        beaconItemSeen.mMinor = random.nextInt();
        beaconItemSeen.mNotification = "Luc suce des noeux " + random.nextInt();
        beaconItemSeen.mRange = random.nextInt(10);

        return beaconItemSeen;
    }
}
