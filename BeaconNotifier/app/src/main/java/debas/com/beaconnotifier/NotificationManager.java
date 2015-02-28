//package debas.com.beaconnotifier;
//
//import android.app.ActivityManager;
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Message;
//import android.support.v4.app.NotificationCompat;
//import android.text.Html;
//
//import org.w3c.dom.Text;
//
//import java.util.ArrayList;
//
///**
// * Created by debas on 28/02/15.
// */
//public class NotificationManager
//{
//
//    public static final int                    NOTIFICATION_ID   = 13374242;
//    public static final ArrayList<PushMessage> PUSH_MESSAGE_LIST = new ArrayList<>();
//
//
//    public static void createNotificationLaunchApp (Context context, PushMessage pushMessage)
//    {
//        PUSH_MESSAGE_LIST.add(pushMessage);
//
//        if (PUSH_MESSAGE_LIST.size() > 1)
//            createCondensedNotification(context, pushMessage);
//        else
//        {
//            //Action sur le clique de la notification
//            Intent i = ActivityManager.getHomeIntent(context);
//            i.putExtra(ActivityManager.EXTRA_FORCE_REFRESH, true);
//
//            PendingIntent contentIntent = PendingIntent
//                    .getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            Intent dismissIntent = new Intent(context, NotificationDismissReceiver.class);
//            PendingIntent dismissPendingIntent = PendingIntent
//                    .getBroadcast(context, 0, dismissIntent, 0);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL)
//                    .setTicker(pushMessage.Title).setContentTitle(pushMessage.Title)
//                    .setContentText(pushMessage.Message).setAutoCancel(true)
//                    .setDeleteIntent(dismissPendingIntent).setContentIntent(contentIntent)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage.Message))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setCategory(NotificationCompat.CATEGORY_SOCIAL)
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//
//            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//
//            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        }
//    }
//
//    public static void createNotificationChatMessage (Context context, PushMessage pushMessage)
//    {
//        PUSH_MESSAGE_LIST.add(pushMessage);
//
//        if (PUSH_MESSAGE_LIST.size() > 1)
//            createCondensedNotification(context, pushMessage);
//        else
//        {
//            int guestId = pushMessage.Datas.optInt("id");
//
//            //Action sur le clique de la notification
//            Intent i = ActivityManager.getChatLaunchIntent(context, guestId);
//            i.putExtra(ActivityManager.EXTRA_FROM_NOTIF, true);
//            PendingIntent contentIntent = PendingIntent
//                    .getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            Intent dismissIntent = new Intent(context, NotificationDismissReceiver.class);
//            PendingIntent dismissPendingIntent = PendingIntent
//                    .getBroadcast(context, 0, dismissIntent, 0);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setDeleteIntent(dismissPendingIntent)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//
//            mBuilder.setTicker(pushMessage.Title).setContentTitle(pushMessage.Title)
//                    .setContentText(Message).setContentIntent(contentIntent)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Message));
//
//
//            String btCancelText = context.getString(R.string.common_cancel);
//            String btChatText = context.getString(R.string.common_chat);
//            ArrayList<PushButton> listBt = new ArrayList<>();
//            if (pushMessage.Buttons != null)
//            {
//                for (PushButton pbt : pushMessage.Buttons)
//                {
//                    switch (pbt.Action)
//                    {
//                        case PushButton.ACTION_CANCEL:
//                            btCancelText = Text;
//                            break;
//
//                        case PushButton.ACTION_MESSAGE:
//                            btChatText = Text;
//                            break;
//                        default:
//                            listBt.add(pbt);
//                            break;
//                    }
//                }
//
//
//                mBuilder
//                        .addAction(new NotificationCompat.Action(R.drawable.action_notif_cancel, btCancelText, dismissPendingIntent));
//                mBuilder
//                        .addAction(new NotificationCompat.Action(R.drawable.action_notif_chat, btChatText, contentIntent));
//
//                for (PushButton pbt : listBt)
//                    mBuilder.addAction(new NotificationCompat.Action(0, Text, contentIntent));
//            }
//
//            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//
//            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        }
//    }
//
//    public static void clearNotification (Context context)
//    {
//        library.manager.NotificationManager.PUSH_MESSAGE_LIST.clear();
//
//        android.app.NotificationManager manager = (android.app.NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(NOTIFICATION_ID);
//
//    }
//
//    private static void createCondensedNotification (Context context, PushMessage pushMessage)
//    {
//
//        int nbNotif = PUSH_MESSAGE_LIST.size();
//
//        Intent dismissIntent = new Intent(context, NotificationDismissReceiver.class);
//        PendingIntent dismissPendingIntent = PendingIntent
//                .getBroadcast(context, 0, dismissIntent, 0);
//
//        //Action sur le clique de la notification
//        Intent i = ActivityManager.getHomeIntent(context);
//        i.putExtra(ActivityManager.EXTRA_FORCE_REFRESH, true);
//        i.putExtra(ActivityManager.EXTRA_FROM_NOTIF, true);
//
//        PendingIntent contentIntent = PendingIntent
//                .getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL)
//                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(contentIntent).setDeleteIntent(dismissPendingIntent)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//
//        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
//        style.setBigContentTitle(context.getString(R.string.notif_multiple_title, nbNotif));
//        for (PushMessage pm : PUSH_MESSAGE_LIST)
//        {
//            style.addLine(Html.fromHtml("<b>" + pm.Title + "</b>  " + Message));
//        }
//
//        mBuilder.setNumber(nbNotif).setTicker(Message)
//                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText(context.getString(R.string.notif_multiple_title, nbNotif))
//                .setStyle(style);
//
//        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }
//
//}