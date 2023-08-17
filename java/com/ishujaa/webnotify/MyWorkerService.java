package com.ishujaa.webnotify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Random;

public class MyWorkerService extends Service {

    String CHANNEL_ID = "com.ishujaa.web_notify";
    DBAccess DBAccess;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WebNotify Channel1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();

        DBAccess = new DBAccess(this);

        Toast.makeText(this, "WebNotifier service resumed.", Toast.LENGTH_SHORT).show();

        new BackScraperAsync(this).execute();



        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartBroadcast.class);
        this.sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int notification_id = new Random().nextInt();
    public void sendNotification(String title, String text, String url){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        Intent intent;
        if(url != null){
            intent = new Intent(this, WebV.class);
            intent.putExtra("url", url);
        }else{
            intent = new Intent(this, MyWorkerService.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notification_id,
                intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        builder.setAutoCancel(true);
        notificationManager.notify(notification_id++, builder.build());
    }

    private boolean scrape(Target target){
        if(isNetworkAvailable()){
            try{
                Document document = Jsoup.connect(target.getUrl()).get();
                Elements primaryContainer = document.select(target.getPrimarySelector());
                Elements targetElements = primaryContainer.select(target.getSecondarySelector());
                String newData = targetElements.html();
                if(!newData.equals(target.getCurrentData())){
                    DBAccess.updateTargetData(target.getId(), newData);
                    target.setCurrentData(newData);
                    return true;
                }
            }catch (Exception e){
                sendNotification("Error", e.getMessage(), null);
            }
        }
        return false;
    }

    private class BackScraperAsync extends AsyncTask<Void, Void, Void> {

        private Context context;
        BackScraperAsync(Context context){
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Target currentTarget = null;
            do{

                try{
                    List<Target> targets = DBAccess.retrieveAllTargets();
                    for(Target target: targets){
                        currentTarget = target;
                        if(target.isEnabled() && scrape(target)){
                            sendNotification(target.getName(), target.getUrl(), target.getUrl());
                        }
                    }
                    new SPHelper(context).writeLastUpdateTime();
                    Thread.sleep(600000);//10 minutes delay for all.

                }catch (Exception e){
                    sendNotification("Error", "For "+currentTarget.getName()+" "+
                            e.getMessage(), null);
                }

            }while (true);
        }
    }
}