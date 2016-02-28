package com.goka.doctor.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.goka.doctor.R;

public class NotificationService {

    private static final String TAG = NotificationService.class.getName();

    private static final int NOTIFICATION_ID = 1;

    public static void show(Context context, PendingIntent pendingIntent, String vendor) {
        String title = context.getResources().getString(R.string.notification_title, vendor);
        String description = context.getResources().getString(R.string.notification_description);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(title);
        builder.setSmallIcon(R.mipmap.small_android);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = getNotificationManager(context);
        notificationManager.notify(TAG, NOTIFICATION_ID, builder.build());
    }

    public static void cancel(Context context) {
        NotificationManager notificationManager = getNotificationManager(context);
        notificationManager.cancel(TAG, NOTIFICATION_ID);
    }

    protected static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
