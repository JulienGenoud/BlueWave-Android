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
    public static String NOTIFICATION_KEY = "notification_key";

    public static List<Prefs.PreferenceFilterBeacon> getFilterBeacon(Context context, TypedArray prefsFilterBeacon) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        List<Prefs.PreferenceFilterBeacon> prefsFilter = new ArrayList<>();
        for (int i = 0; i < prefsFilterBeacon.length(); i++) {
            Prefs.PreferenceFilterBeacon preferenceFilterBeacon = new Prefs.PreferenceFilterBeacon();
            String title = prefsFilterBeacon.getString(i);
            preferenceFilterBeacon.Title = title;
            preferenceFilterBeacon.Checked = sharedPreferences.getBoolean(title, false);
            prefsFilter.add(preferenceFilterBeacon);
        }
        return prefsFilter;
    }

    public static void setBeaconFilter(Context context, String key, boolean selected) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, selected);
        editor.apply();
    }

    public static void setNoticationEnable(Context context, boolean bool) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_KEY, bool);
        editor.apply();
    }

    public static boolean getNoticationEnable(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_KEY, true);
    }
}
