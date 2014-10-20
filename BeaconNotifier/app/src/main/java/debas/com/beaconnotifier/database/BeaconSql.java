package debas.com.beaconnotifier.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by debas on 14/10/14.
 */
public class BeaconSql extends SQLiteOpenHelper {

//    private static final String TEST = "CREATE TABLE table_contact (id INTEGER PRIMARY KEY, name TEXT, phone_number TEXT)";

    private static final String CREATE_BDD = "CREATE TABLE IF NOT EXISTS " + BeaconDataBase.BEACONS_TABLE + " " +
            "(beacon_id int(11) NOT NULL," +
            " customer_id int(11) NOT NULL," +
            " uuid text NOT NULL," +
            " major int(11) NOT NULL," +
            " minor int(11) NOT NULL," +
            " action int(11) NOT NULL," +
            " notification text NOT NULL," +
            " content text NOT NULL," +
            " range int(11) NOT NULL," +
            " lastUpdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP)";

    public BeaconSql(Context context, String dbName, CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        Log.d("coucou", "coucou");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("coucou", Boolean.toString(sqLiteDatabase.isDatabaseIntegrityOk()));
        sqLiteDatabase.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeaconDataBase.BEACONS_TABLE);
        onCreate(sqLiteDatabase);
    }
}
