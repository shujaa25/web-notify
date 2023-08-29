package com.ishujaa.webnotify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class MyNotificationHelper {
    private final String CHANNEL_ID = "com.ishujaa.web_notify";
    private final Context context;
    private int notification_id = new Random().nextInt();
    ;

    MyNotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WebNotify Updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }else{
            //handle
        }
    }

    public void postNotification(String title, String text, String url) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        Intent intent;
        if (url != null) {
            intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", url);
        } else {
            intent = new Intent(context, MyWorkerService.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notification_id,
                intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setAutoCancel(true);

        notificationManager.notify(notification_id++, builder.build());
    }
}
