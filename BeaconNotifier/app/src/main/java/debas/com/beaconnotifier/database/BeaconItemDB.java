package debas.com.beaconnotifier.database;

import android.content.ContentValues;

import com.google.gson.JsonArray;

/**
 * Created by debas on 21/10/14.
 */

public class BeaconItemDB {

    private ContentValues contentValues = new ContentValues();
    public static String BEACON_ID = "beacon_id";
    public static String CUSTOMER_ID = "customer_id";
    public static String UUID = "uuid";
    public static String MAJOR = "major";
    public static String MINOR = "minor";
    public static String ACTION = "action";
    public static String NOTIFICATION = "notification";
    public static String CONTENT = "content";
    public static String RANGE = "range";
    public static String LAST_UPDATE = "lastUpdate";

    public BeaconItemDB(JsonArray jsonArray) {
        contentValues.put(BEACON_ID, jsonArray.get(0).getAsString());
        contentValues.put(UUID, jsonArray.get(1).getAsString());
        contentValues.put(MAJOR, Integer.parseInt(jsonArray.get(2).getAsString()));
        contentValues.put(MINOR, Integer.parseInt(jsonArray.get(3).getAsString()));
        contentValues.put(NOTIFICATION, jsonArray.get(4).getAsString());
        contentValues.put(RANGE, Integer.parseInt(jsonArray.get(5).getAsString()));
    }

    public Object getValue(String key) {
        return contentValues.get(key);
    }

    public ContentValues getContent() {
        return contentValues;
    }
}
