package com.rsm.rsmauthenticator;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

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
        createNotification(from, otp, time, userFullName, appName, notificationTitle, method, requestId);
    }

    // this method will create a notification from the message recieved
    private void createNotification(String from, String body, String time, String userName, String appName, String notificationTitle, String method, String requestId) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(notificationTitle)
                .setContentText("Access Request for " + appName);

        CreateIntent(mBuilder, body, time, userName, appName, method, requestId);

        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE); vibrator.vibrate(2000);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notification_Id, mBuilder.build());
    }

    private void CreateIntent(NotificationCompat.Builder mBuilder, String body, String time, String userName, String appName, String method, String requestId) {
        mBuilder.setTicker("RSM Authentication Required");
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setOnlyAlertOnce(true);
        //need to get vibrations working

        Intent resultIntent;

        if(method.equals("OneTimePasscode")){
            resultIntent = new Intent(this, AccessRequestActivity.class);
        }else if (method.equals("PushNotification")){
            resultIntent = new Intent(this, AccessResponseActivity.class);
        }else resultIntent = new Intent(this, MainActivity.class);//if no method is set then just send the user to the main activity

        resultIntent.putExtra("otp", body);
        resultIntent.putExtra("requestTime", time);
        resultIntent.putExtra("username", userName);
        resultIntent.putExtra("appName", appName);
        resultIntent.putExtra("requestId", requestId);

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