package debas.com.beaconnotifier.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.security.Timestamp;
import java.util.Date;

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

    public long insertBeacon() {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put("beacon_id", 10);
        values.put("customer_id", 10);
        values.put("uuid", "53168465-4534-6543-2134-546865413213");
        values.put("major", 10);
        values.put("minor", 1);
        values.put("action", 1);
        values.put("notification", "Coucou la famille");
        values.put("content", "Je t'aime");
        values.put("range", 4);
        values.put("lastUpdate", 123456789);

        //on insère l'objet dans la BDD via le ContentValues
        return mDataBase.insert(BEACONS_TABLE, null, values);
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

}
