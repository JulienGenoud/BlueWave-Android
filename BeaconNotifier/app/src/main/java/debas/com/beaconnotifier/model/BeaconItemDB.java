package debas.com.beaconnotifier.model;

import com.google.gson.JsonArray;
import com.orm.SugarRecord;

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

    public BeaconItemDB() {

    }

    public BeaconItemDB(JsonArray jsonArray) {
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
}
