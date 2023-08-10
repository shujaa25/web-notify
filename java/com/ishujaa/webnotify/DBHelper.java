package com.ishujaa.webnotify;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "scraper";
    private static final int DB_VERSION = 32;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE target_table (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "url TEXT NOT NULL," +
                "primary_selector TEXT NOT NULL," +
                "secondary_selector TEXT NOT NULL," +
                "group_selector TEXT," +
                "data TEXT," +
                "enabled BOOLEAN);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("ALTER TABLE target_table ADD COLUMN enabled BOOLEAN;");
        //sqLiteDatabase.execSQL("UPDATE target_table SET enabled = true;");
    }

    public void insertRecord(SQLiteDatabase db, String name, String url, String primary_selector,
                             String secondary_selector, String group_selector,
                             String data, boolean enabled){
        ContentValues targetValues = new ContentValues();
        targetValues.put("name", name);
        targetValues.put("url", url);
        targetValues.put("primary_selector", primary_selector);
        targetValues.put("secondary_selector", secondary_selector);
        targetValues.put("group_selector", group_selector);
        targetValues.put("data", data);
        targetValues.put("enabled", enabled);
        db.insert("target_table", null, targetValues);
    }
}
