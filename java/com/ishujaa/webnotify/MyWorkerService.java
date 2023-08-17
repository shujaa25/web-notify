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


    private DBAccess DBAccess;
    private MyNotification notification;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notification = new MyNotification(this);
        DBAccess = new DBAccess(this);

        Toast.makeText(this, "WebNotifier service resumed.", Toast.LENGTH_SHORT).show();

        new BackScraperAsync(this).execute();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!destroy){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, RestartBroadcast.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                notification.postNotification("Error", e.getMessage(), null);
            }
        }
        return false;
    }
    private boolean destroy = false;
    private void stopWorkerService(){
        destroy = true;
        stopSelf();
    }

    private class BackScraperAsync extends AsyncTask<Void, Void, Void> {

        private Context context;
        BackScraperAsync(Context context){
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Target currentTarget = null;
            do {

                try {
                    List<Target> targets = DBAccess.retrieveAllTargets();
                    if (targets.size() == 0) {
                        notification.postNotification("No targets found.", "Please add targets", null);
                        stopWorkerService();
                        break;
                    } else {
                        for (Target target : targets) {
                            currentTarget = target;
                            if (target.isEnabled() && scrape(target)) {
                                notification.postNotification(target.getName(), target.getUrl(), target.getUrl());
                            }
                        }
                        new SPHelper(context).writeLastUpdateTime();
                        Thread.sleep(600000);//10 minutes delay for all.
                    }

                } catch (Exception e) {
                    notification.postNotification("Error", "For " + currentTarget.getName() + " " +
                            e.getMessage(), null);
                }

            } while (true);
            return null;
        }
    }
}