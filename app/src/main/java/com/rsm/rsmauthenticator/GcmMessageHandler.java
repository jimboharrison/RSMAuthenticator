package com.rsm.rsmauthenticator;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

public class GcmMessageHandler extends GcmListenerService {
    public static final int notification_Id = 123456;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String otp = data.getString("otp");
        String time = data.getString("time");
        String userFullName = data.getString("username");
        String appName = data.getString("app");
        String method = data.getString("method");
        String requestId = data.getString("id");

        String notificationTitle = "RSM Authentication";

        if(method.equals("OneTimePasscode")){
            createNotification(from, otp, time, userFullName, appName, notificationTitle, method, requestId);
        }else if (method.equals("PushNotification")){
            createHeadsUpNotification(from, otp, time, userFullName, appName, notificationTitle, requestId);
        }

    }

    // this method will create a notification from the message recieved

    private void createHeadsUpNotification(String from, String otp, String time, String userFullName, String appName, String notificationTitle, String requestId) {

        int id = new Random().nextInt();

        //setting up approve action for notification
        Intent approveAction = new Intent(this, MobileResponseService.class);
        approveAction.setAction(MobileResponseService.ApproveAction);
        approveAction.putExtra("otp", otp);
        approveAction.putExtra("requestTime", time);
        approveAction.putExtra("username", userFullName);
        approveAction.putExtra("appName", appName);
        approveAction.putExtra("requestId", requestId);
        approveAction.putExtra("notificationId", Integer.toString(id));
        PendingIntent piApproveAction = PendingIntent.getService(this, 0, approveAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting up decline action for notification
        Intent declineAction = new Intent(this, MobileResponseService.class);
        declineAction.setAction(MobileResponseService.DeclineAction);
        declineAction.putExtra("otp", otp);
        declineAction.putExtra("requestTime", time);
        declineAction.putExtra("username", userFullName);
        declineAction.putExtra("appName", appName);
        declineAction.putExtra("requestId", requestId);
        declineAction.putExtra("notificationId", Integer.toString(id));
        PendingIntent piDeclineAction = PendingIntent.getService(this, 0, declineAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.notificationicon)
                .setContentTitle(notificationTitle)
                .setContentText("For " + appName + " : " + otp)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_tick, "Approve", piApproveAction)
                .addAction(R.drawable.ic_cross, "Decline", piDeclineAction);

        //if the main notification is clicked
        CreateIntent(mBuilder, otp, time, userFullName, appName, "PushNotification", requestId, notification_Id);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    private void createNotification(String from, String body, String time, String userName, String appName, String notificationTitle, String method, String requestId) {
        Context context = getBaseContext();
        int notificationId = new Random().nextInt();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.notificationicon).setContentTitle(notificationTitle)
                .setContentText("Access Request for " + appName);

        CreateIntent(mBuilder, body, time, userName, appName, method, requestId, notificationId);

        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE); vibrator.vibrate(2000);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notification_Id, mBuilder.build());
    }

    private void CreateIntent(NotificationCompat.Builder mBuilder, String body, String time, String userName, String appName, String method, String requestId, int notificationId) {
        mBuilder.setTicker("RSM Authentication Required");
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setAutoCancel(true);
        //need to get vibrations working

        Intent resultIntent;

        if(method.equals("OneTimePasscode")){
            resultIntent = new Intent(this, AccessRequestActivity.class);
        }else if(method.equals("PushNotification")){
            resultIntent = new Intent(this, AccessResponseActivity.class);
        }else{
            resultIntent = new Intent(this, MainActivity.class);//if no method is set then just send the user to the main activity
        }

        resultIntent.putExtra("otp", body);
        resultIntent.putExtra("requestTime", time);
        resultIntent.putExtra("username", userName);
        resultIntent.putExtra("appName", appName);
        resultIntent.putExtra("requestId", requestId);
        resultIntent.putExtra("notificationId", Integer.toString(notificationId));

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
    }
}