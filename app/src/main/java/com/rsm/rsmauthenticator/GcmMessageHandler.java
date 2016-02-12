package com.rsm.rsmauthenticator;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 123456;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String otp = data.getString("otp");
        String userFullName = data.getString("username");
        String notificationTitle = "Acesss request for User";
        createNotification(from, otp, notificationTitle);
    }

    // this method will create a notification from the message recieved
    private void createNotification(String from, String body, String notificationTitle) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(notificationTitle)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}