package debas.com.beaconnotifier.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import debas.com.beaconnotifier.AsyncTaskDB;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemDB;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.preferences.PreferencesHelper;
import debas.com.beaconnotifier.utils.Constants;

/**
 * Created by debas on 14/10/14.
 */
public class BeaconDataBase {
    private SQLiteDatabase mDataBase = null;
    private static BeaconDataBase mBeaconDB = null;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "beacons.db";
    public static final String BEACONS_TABLE = "beacons";

    private BeaconDataBase() {
    }

    public static BeaconDataBase getInstance(Context context) {
        if (mBeaconDB == null) {
            mBeaconDB = new BeaconDataBase();
        }
        return mBeaconDB;
    }

    /* update new beacon on api and update new time*/
    public  void updateDB(final Context context, final AsyncTaskDB.OnDBUpdated listener) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long lastTimeUpdate = sharedPreferences.getLong(PreferencesHelper.LAST_TIME_UPDATE_DB, 0) / 1000;

        Ion.with(context)
                .load(Constants.URL_API_DB + String.valueOf(lastTimeUpdate))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {

                        if (error != null) {
                            listener.onDBUpdated(false, 0);
                            return;
                        }
                        JsonArray beacons = result.getAsJsonArray("e");

                        for (int i = 0; i < beacons.size(); i++) {
                            BeaconItemDB beaconItemDB = new BeaconItemDB(beacons.get(i).getAsJsonArray());
                            Select select = Select.from(BeaconItemDB.class).where(Condition.prop("M_UUID").eq(beaconItemDB.mUuid),
                                    Condition.prop("M_MAJOR").eq(beaconItemDB.mMajor), Condition.prop("M_MINOR").eq(beaconItemDB.mMinor));
                            if (select.count() > 0) {
                                List beaconItemDBList = select.list();
                                for (Object b : beaconItemDBList) {
                                    ((BeaconItemDB)b).delete();
                                }
                                Class classType = BeaconItemSeen.class;
                                select = Select.from(classType).where(Condition.prop("M_UUID").eq(beaconItemDB.mUuid),
                                        Condition.prop("M_MAJOR").eq(beaconItemDB.mMajor), Condition.prop("M_MINOR").eq(beaconItemDB.mMinor));
                                if (select.count() > 0) {
                                    BeaconItemSeen beaconItemSeen = (BeaconItemSeen) select.first();
                                    beaconItemSeen.updateField(beaconItemDB);
                                    beaconItemSeen.save();
                                }
                            }
                            beaconItemDB.save();
                        }

                        List<BeaconItemDB> beaconItemDBList = BeaconItemDB.listAll(BeaconItemDB.class);

                        long newLastTimeUpdate = result.get("t").getAsLong();
                        PreferencesHelper.setLastUpdateDB(context, newLastTimeUpdate * 1000);

                        listener.onDBUpdated(true, beaconItemDBList.size());
                    }
                });
    }

}
