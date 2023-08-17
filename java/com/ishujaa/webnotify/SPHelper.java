package com.ishujaa.webnotify;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SPHelper {

    private final String PREF_FILE_KEY = "com.ishujaa.webnotify.MY_PREFERENCE_FILE_KEY";
    private final String LAST_UPDATE_KEY = "com.ishujaa.webnotify.LAST_UPDATE_KEY";
    private final String DELAY_KEY = "com.ishujaa.webnotify.DELAY";
    private final Context context;
    SPHelper(Context context){
        this.context = context;
    }

    public long getDelay(){
        SharedPreferences preferences = context.getSharedPreferences(
                PREF_FILE_KEY, Context.MODE_PRIVATE);
        return preferences.getLong(DELAY_KEY, 600000);//default = 10min
    }

    public void setDelay(long millis){
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(DELAY_KEY, millis);
        editor.apply();
    }

    public void writeLastUpdateTime(){
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yy HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);

        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_UPDATE_KEY, time);
        editor.apply();
    }

    public String getLastUpdateTime(){
        SharedPreferences preferences = context.getSharedPreferences(
                PREF_FILE_KEY, Context.MODE_PRIVATE);
        return preferences.getString(LAST_UPDATE_KEY, "not available.");
    }

}
