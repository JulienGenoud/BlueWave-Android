package debas.com.beaconnotifier.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.utils.Constants;

/**
 * Created by debas on 14/10/14.
 */
public class BeaconDataBase {
    private BeaconSql mBeaconSql = null;
    private SQLiteDatabase mDataBase = null;
    private static BeaconDataBase mBeaconDB = null;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "beacons.db";
    public static final String BEACONS_TABLE = "beacons";

    private BeaconDataBase(Context context) {
        mBeaconSql = new BeaconSql(context, DB_NAME, null, DB_VERSION);
    }

    public static BeaconDataBase getInstance(Context context) {
        if (mBeaconDB == null) {
            mBeaconDB = new BeaconDataBase(context);
        }
        return mBeaconDB;
    }

    public void open(){
        //on ouvre la BDD en écriture
        mDataBase = mBeaconSql.getReadableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
//        mDataBase.close();
    }

    public SQLiteDatabase getBDD(){
        return mDataBase;
    }

    public void insertBeacon(List<BeaconItemDB> beaconItemDBList) {
        open();
        for (BeaconItemDB b : beaconItemDBList) {
            if (b.getContent() != null) {
                mDataBase.insertWithOnConflict(BEACONS_TABLE, null, b.getContent(), SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        close();
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
    public  void updateDB(final Context context, final SharedPreferences sharedPreferences, final AsyncTaskDB.OnDBUpdated listener) {
        long lastTimeUpdate = sharedPreferences.getLong(Constants.LAST_TIME_UPDATE_DB, 0);

        Ion.with(context)
                .load(Constants.URL_API_DB + String.valueOf(lastTimeUpdate))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        System.out.println("error : " + error);

                        if (error != null) {
                            listener.onDBUpdated(false, 0);
                            return;
                        }
                        JsonArray beacons = result.getAsJsonArray("e");
                        List<BeaconItemDB> beaconItemDBList = new ArrayList<BeaconItemDB>();
                        for (int i = 0; i < beacons.size(); i++) {
                            beaconItemDBList.add(new BeaconItemDB(beacons.get(i).getAsJsonArray()));
                        }

                        /* insert new beacons on database */
                        insertBeacon(beaconItemDBList);

                        long newLastTimeUpdate = result.get("t").getAsLong();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(Constants.LAST_TIME_UPDATE_DB, newLastTimeUpdate).apply();

                        listener.onDBUpdated(true, beaconItemDBList.size());

                        System.out.println("result : " + beacons);
                        System.out.println("time : " + newLastTimeUpdate);
                    }
                });
    }

}
