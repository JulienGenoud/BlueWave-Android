package debas.com.beaconnotifier.model;

import com.google.gson.JsonArray;
import com.orm.SugarRecord;

import org.altbeacon.beacon.Beacon;

import java.io.Serializable;

/**
 * Created by debas on 21/10/14.
 */

public class BeaconItemDB extends SugarRecord<BeaconItemDB> implements Serializable {

    public String mUuid = "";
    public String mNotification = "";
    public String mTitle = "";
    public String mSerial = "";

    public int mMajor = 0;
    public int mMinor = 0;
    public int mRange = 0;

    public BeaconItemDB() {

    }

    public BeaconItemDB(JsonArray jsonArray) {
        mSerial = jsonArray.get(0).getAsString();
        mUuid = jsonArray.get(1).getAsString().toLowerCase();
        mMajor = jsonArray.get(2).getAsInt();
        mMinor = jsonArray.get(3).getAsInt();
        mNotification = jsonArray.get(4).getAsString();
        mRange = jsonArray.get(5).getAsInt();
        mTitle = jsonArray.get(6).getAsString();
    }

    public boolean compare(Beacon beacon) {
        return (mUuid.equalsIgnoreCase(beacon.getId1().toUuidString())
                && mMajor == beacon.getId2().toInt()
                && mMinor == beacon.getId3().toInt());
    }
}
