package debas.com.beaconnotifier.database;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public BeaconItemDB(JSONArray jsonArray) {

        try {
            contentValues.put(BEACON_ID, Integer.parseInt(jsonArray.getString(0)));
            contentValues.put(UUID, jsonArray.getString(1));
            contentValues.put(MAJOR, Integer.parseInt(jsonArray.getString(2)));
            contentValues.put(MINOR, Integer.parseInt(jsonArray.getString(3)));
            contentValues.put(NOTIFICATION, jsonArray.getString(4));
            contentValues.put(RANGE, Integer.parseInt(jsonArray.getString(5)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ContentValues getContent() {
        return contentValues;
    }
}
