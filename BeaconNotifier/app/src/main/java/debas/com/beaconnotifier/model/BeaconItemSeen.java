package debas.com.beaconnotifier.model;

import android.database.Cursor;

import com.orm.dsl.Ignore;

import java.util.Random;

/**
 * Created by debas on 19/02/15.
 */
public class BeaconItemSeen extends BeaconItemDB {
    public boolean mFavorites = false;
    public long mSeen = 0;
    public boolean mConsulted = false;

    @Ignore
    public double mDistance = 0;

    public BeaconItemSeen() {

    }

    public BeaconItemSeen(BeaconItemDB beaconItemDB) {
        updateField(beaconItemDB);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BeaconItemSeen)) {
            return false;
        }
        BeaconItemSeen b = (BeaconItemSeen) o;
        if (this.mUuid.equalsIgnoreCase(b.mUuid)
                && this.mBeaconId.equalsIgnoreCase(b.mBeaconId)
                && this.mMinor == b.mMinor
                && this.mMajor == b.mMajor)
            return true;
        return false;
    }

    public static BeaconItemSeen fromCursor(Cursor cursor) {
        BeaconItemSeen beaconItemSeen = new BeaconItemSeen();

        beaconItemSeen.mSeen = cursor.getLong(cursor.getColumnIndex("M_SEEN"));
        beaconItemSeen.mFavorites = (cursor.getInt(cursor.getColumnIndex("M_FAVORITES")) == 1);
        beaconItemSeen.mBeaconId = cursor.getString(cursor.getColumnIndex("M_BEACON_ID"));
        beaconItemSeen.mUuid = cursor.getString(cursor.getColumnIndex("M_UUID"));
        beaconItemSeen.mNotification = cursor.getString(cursor.getColumnIndex("M_NOTIFICATION"));
        beaconItemSeen.mMajor = cursor.getInt(cursor.getColumnIndex("M_MAJOR"));
        beaconItemSeen.mMinor = cursor.getInt(cursor.getColumnIndex("M_MINOR"));
        beaconItemSeen.mRange = cursor.getInt(cursor.getColumnIndex("M_RANGE"));
        beaconItemSeen.mConsulted = (cursor.getInt(cursor.getColumnIndex("M_CONSULTED")) == 1);

        return beaconItemSeen;
    }

    public void updateField(BeaconItemDB beaconItemDB) {
        mBeaconId = beaconItemDB.mBeaconId;
        mUuid = beaconItemDB.mUuid;
        mMajor = beaconItemDB.mMajor;
        mMinor = beaconItemDB.mMinor;
        mNotification = beaconItemDB.mNotification;
        mRange = beaconItemDB.mRange;
    }
}
