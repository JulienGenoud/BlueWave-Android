package debas.com.beaconnotifier.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.JSONParserURL;

/**
 * Created by debas on 14/10/14.
 */
public class BeaconDataBase {
    private BeaconSql mBeaconSql = null;
    private SQLiteDatabase mDataBase = null;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "beacons.db";
    public static final String BEACONS_TABLE = "beacons";

    public BeaconDataBase(Context context) {
        mBeaconSql = new BeaconSql(context, DB_NAME, null, DB_VERSION);
    }

    public void open(){
        //on ouvre la BDD en écriture
        mDataBase = mBeaconSql.getReadableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        mDataBase.close();
    }

    public SQLiteDatabase getBDD(){
        return mDataBase;
    }

    public void insertBeacon(List<BeaconItemDB> beaconItemDBList) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
//        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
//        values.put("beacon_id", 10);
//        values.put("customer_id", 10);
//        values.put("uuid", "53168465-4534-6543-2134-546865413213");
//        values.put("major", 10);
//        values.put("minor", 1);
//        values.put("action", 1);
//        values.put("notification", "Coucou la famille");
//        values.put("content", "Je t'aime");
//        values.put("range", 4);
//        values.put("lastUpdate", 123456789);

        for (BeaconItemDB b : beaconItemDBList) {
            if (b.getContent() == null) {
                Log.d("Test", "null");
            }
            mDataBase.insert(BEACONS_TABLE, null, b.getContent());
        }
    }

    public boolean isBeaconWithUUID(String uuid) {
        //Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
//        String request = "SELECT * FROM " + BEACONS_TABLE + " WHERE uuid LIKE " + uuid;
        Cursor c = mBeaconSql.getReadableDatabase().query(BEACONS_TABLE, new String[] {"uuid"}, "uuid =?", new String[] {uuid}, null, null, null, null);
//        Cursor c = mDataBase.rawQuery(request, null);

        if (c.moveToFirst()) {
            do {
                Log.d("result", c.getString(0));
            } while (c.moveToNext());
        }

        return c.getCount() > 0 ? true : false;
    }

    /* update new beacon on api and update new time*/
    public  void updateDB(final Activity activity, final SharedPreferences sharedPreferences, final AsyncTaskDB.OnDBUpdated listener) {
        long lastTimeUpdate = sharedPreferences.getLong(Constants.LAST_TIME_UPDATE_DB, 0);

        JSONParserURL jsonParserURL = new JSONParserURL(new AsyncTaskDB.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if (jsonObject == null) {
                    listener.onDBUpdated(false, 0);
                }
                try {
                    List<BeaconItemDB> beaconItemDBList = new ArrayList<BeaconItemDB>();
                    JSONArray jsonArray = jsonObject.getJSONArray("e");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray beaconContent = jsonArray.getJSONArray(i);
                        beaconItemDBList.add(new BeaconItemDB(beaconContent));
                    }
                    insertBeacon(beaconItemDBList);

                    /* debug */
                    if (activity != null) Toast.makeText(activity, "updated : " + String.valueOf(beaconItemDBList.size()) + " elements", Toast.LENGTH_LONG).show();

                    long newLastTimeUpdate = jsonObject.getLong("t");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(Constants.LAST_TIME_UPDATE_DB, newLastTimeUpdate).commit();

                    listener.onDBUpdated(true, beaconItemDBList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onDBUpdated(false, 0);
                }
            }
        });
        jsonParserURL.execute(Constants.URL_API_DB + String.valueOf(lastTimeUpdate));
    }

}
