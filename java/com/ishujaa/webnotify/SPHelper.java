package com.ishujaa.webnotify;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SPHelper {

    String PREF_FILE_KEY = "com.ishujaa.scraper.MY_PREFERENCE_FILE_KEY";
    Context context;
    SPHelper(Context context){
        this.context = context;
    }

    String LAST_UPDATE_KEY = "com.ishujaa.scraper.LAST_UPDATE_KEY";

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
