package debas.com.beaconnotifier;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import debas.com.beaconnotifier.display.BeaconActivity;
import debas.com.beaconnotifier.display.MainActivity;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
* Created by debas on 28/02/15.
*/
public class NotificationManager
{

    public static final int                    NOTIFICATION_ID   = 13374242;
    public static final ArrayList<BeaconItemSeen> PUSH_MESSAGE_LIST = new ArrayList<>();

    public static void createNotificationLaunchApp (Context context, BeaconItemSeen beaconItemSeen)
    {
        PUSH_MESSAGE_LIST.add(beaconItemSeen);

        if (PUSH_MESSAGE_LIST.size() > 1)
            createCondensedNotification(context, beaconItemSeen);
        else
        {
            //Action sur le clique de la notification
            Intent i = new Intent(context, BeaconActivity.class);
            i.putExtra(BeaconActivity.EXTRA_BEACON_UUID, beaconItemSeen.mUuid);
            i.putExtra(BeaconActivity.EXTRA_BEACON_MAJOR, beaconItemSeen.mMajor);
            i.putExtra(BeaconActivity.EXTRA_BEACON_MINOR, beaconItemSeen.mMinor);
            i.putExtra("POS", 0);
            i.putExtra("FROM_NOTIFICATION", true);

            PendingIntent contentIntent = PendingIntent
                    .getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon_bluewave).setDefaults(Notification.DEFAULT_ALL)
                    .setTicker(context.getString(R.string.app_name)).setContentTitle(context.getString(R.string.app_name))
                    .setContentText(beaconItemSeen.mNotification).setAutoCancel(true).setContentIntent(contentIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(beaconItemSeen.mNotification))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    public static void clearNotification (Context context)
    {
        PUSH_MESSAGE_LIST.clear();

        android.app.NotificationManager manager = (android.app.NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);

    }

    private static void createCondensedNotification (Context context, BeaconItemSeen beaconItemSeen)
    {

        int nbNotif = PUSH_MESSAGE_LIST.size();

        //Action sur le clique de la notification
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("FROM_NOTIFICATION", true);
        i.putExtra(MainActivity.LAUNCH_PAGE, MainActivity.HISTORY_PAGE);

        PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_bluewave).setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(context.getString(R.string.app_name, nbNotif));
        for (BeaconItemSeen b : PUSH_MESSAGE_LIST)
        {
            style.addLine(b.mNotification);
        }

        mBuilder.setNumber(nbNotif).setTicker(beaconItemSeen.mNotification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_name, nbNotif))
                .setStyle(style);

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}