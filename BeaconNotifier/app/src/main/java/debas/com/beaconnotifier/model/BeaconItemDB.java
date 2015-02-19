package debas.com.beaconnotifier.model;

import com.google.gson.JsonArray;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.altbeacon.beacon.Beacon;

/**
 * Created by debas on 21/10/14.
 */

public class BeaconItemDB extends SugarRecord<BeaconItemDB> {

    public String mBeaconId = "";
    public String mUuid = "";
    public String mNotification = "";

    public int mMajor = 0;
    public int mMinor = 0;
    public int mRange = 0;

    @Ignore
    private double mDistance = 0;

    public BeaconItemDB() {

    }

    public BeaconItemDB(JsonArray jsonArray, double distance) {
        mDistance = distance;

        mBeaconId = jsonArray.get(0).getAsString();
        mUuid = jsonArray.get(1).getAsString().toLowerCase();
        mMajor = jsonArray.get(2).getAsInt();
        mMinor = jsonArray.get(3).getAsInt();
        mNotification = jsonArray.get(4).getAsString();
        mRange = jsonArray.get(5).getAsInt();
    }

    public boolean compare(Beacon beacon) {
        return (mUuid.equalsIgnoreCase(beacon.getId1().toUuidString())
                && mMajor == beacon.getId2().toInt()
                && mMinor == beacon.getId3().toInt());
    }

    public double getDistance() {
        return this.mDistance;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }
}
