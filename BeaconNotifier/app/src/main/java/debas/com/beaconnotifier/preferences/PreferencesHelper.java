package debas.com.beaconnotifier.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 02/03/15.
 */
public class PreferencesHelper {
    public static final String LAST_TIME_UPDATE_DB = "last_update_db";
    public static final String NOTIFICATION_KEY = "notification_key";
    public static final String FIRST_LAUNCH_KEY = "first_launched";

    public static List<Prefs.PreferenceFilterBeacon> getFilterBeacon(Context context, TypedArray prefsFilterBeacon) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        List<Prefs.PreferenceFilterBeacon> prefsFilter = new ArrayList<>();
        for (int i = 0; i < prefsFilterBeacon.length(); i++) {
            Prefs.PreferenceFilterBeacon preferenceFilterBeacon = new Prefs.PreferenceFilterBeacon();
            String title = prefsFilterBeacon.getString(i);
            preferenceFilterBeacon.Title = title;
            preferenceFilterBeacon.Checked = sharedPreferences.getBoolean(title, true);
            prefsFilter.add(preferenceFilterBeacon);
        }
        return prefsFilter;
    }

    public static void setBeaconFilter(Context context, String key, boolean selected) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, selected);
        editor.apply();
    }

    public static void setNoticationEnable(Context context, boolean bool) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_KEY, bool);
        editor.apply();
    }

    public static boolean getNoticationEnable(Context context) {
        if (context == null)
            return true;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_KEY, true);
    }

    public static void setLastUpdateDB(Context context, long lastUpdate) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME_UPDATE_DB, lastUpdate);
        editor.apply();
    }

    public static long getLastUpdateDB(Context context) {
        if (context == null)
            return -1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_TIME_UPDATE_DB, -1);
    }

    public static void setFirstLaunch(Context context, boolean bool) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH_KEY, bool);
        editor.apply();
    }

    public static boolean getFirstLaunch(Context context) {
        if (context == null)
            return false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);
    }
}