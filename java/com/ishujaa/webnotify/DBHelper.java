package com.ishujaa.webnotify;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "web_notify";
    private static final int DB_VERSION = 1;

    public static final String TARGET_TABLE_NAME = "target_table";

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

    }
}
