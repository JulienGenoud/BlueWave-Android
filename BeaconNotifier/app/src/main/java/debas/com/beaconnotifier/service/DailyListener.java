package debas.com.beaconnotifier.service;

/**
 * Created by debas on 15/10/14.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;

public class DailyListener implements WakefulIntentService.AlarmListener {
    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context context) {
        // register when enabled in preferences
//        if (PreferenceHelper.getUpdateCheckDaily(context)) {
            Log.i("DailyListener", "Schedule update check...");

            // every day at 9 am
            Calendar calendar = Calendar.getInstance();
            // if it's after or equal 9 am schedule for next day
//            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 9) {
//                calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
//            }
//            calendar.set(Calendar.HOUR_OF_DAY, 9);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);

            mgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    60 * 10 * 1000, pi);
//        }
    }

    public void sendWakefulWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // only when connected or while connecting...
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

//            boolean updateOnlyOnWifi = PreferenceHelper.getUpdateOnlyOnWifi(context);

            // if we have mobile or wifi connectivity...
//            if (((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) && updateOnlyOnWifi == false)
//                    || (netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            if ((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) || (netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                Log.d("DailyListener", "We have internet, start update check directly now!");

                Intent backgroundIntent = new Intent(context, PullBeaconService.class);
                WakefulIntentService.sendWakefulWork(context, backgroundIntent);
            } else {
                Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

                // enable receiver to schedule update when internet is available!
                ConnectivityReceiver.enableReceiver(context);
            }
        } else {
            Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

            // enable receiver to schedule update when internet is available!
            ConnectivityReceiver.enableReceiver(context);
        }
    }

    public long getMaxAge() {
        return (60 * 10 * 1000);
    }
}