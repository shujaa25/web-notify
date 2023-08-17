package com.ishujaa.webnotify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBAccess {
    private final Context context;
    public String TARGET_TABLE_NAME = "target_table";
    public String LOG_TABLE_NAME = "log_table";
    SQLiteOpenHelper sqLiteOpenHelper;
    DBAccess(Context context){
        this.context = context;
        sqLiteOpenHelper = new DBHelper(context);
    }

    public Cursor getTargetNamesCursor() throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("target_table",
                new String[]{"_id, name, enabled"},
                null, null, null, null, "enabled ASC");
        return cursor;
    }

    public Target getTargetFields(int targetId) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("target_table",
                new String[]{"name, url, primary_selector," +
                        "secondary_selector, group_selector," +
                        "data, enabled"}, "_id=?",
                new String[]{Integer.toString(targetId)},
                null, null, null);

        cursor.moveToFirst();

        Target target = new Target();

        target.setName(cursor.getString(0));
        target.setUrl(cursor.getString(1));
        target.setPrimarySelector(cursor.getString(2));
        target.setSecondarySelector(cursor.getString(3));
        target.setGroupSelector(cursor.getString(4));
        target.setCurrentData(cursor.getString(5));

        if(cursor.getString(6).equals("1")){
            target.setEnabled(true);
        }else target.setEnabled(false);

        cursor.close();
        database.close();

        return target;
    }

    public void insertTarget(String name, String url, String primary_selector,
                             String secondary_selector, String group_selector,
                             String data, boolean enabled){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues targetValues = new ContentValues();
        targetValues.put("name", name);
        targetValues.put("url", url);
        targetValues.put("primary_selector", primary_selector);
        targetValues.put("secondary_selector", secondary_selector);
        targetValues.put("group_selector", group_selector);
        targetValues.put("data", data);
        targetValues.put("enabled", enabled);
        database.insert("target_table", null, targetValues);

        database.close();
    }

    public void updateTargetFields(String name, String url, String primary_selector,
                                   String secondary_selector, String group_selector,
                                   String data, boolean enabled, int targetId){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        ContentValues targetValues = new ContentValues();
        targetValues.put("name", name);
        targetValues.put("url", url);
        targetValues.put("primary_selector", primary_selector);
        targetValues.put("secondary_selector", secondary_selector);
        targetValues.put("group_selector", group_selector);
        targetValues.put("data", data);
        targetValues.put("enabled", enabled);

        database.update("target_table", targetValues, "_id=?",
                new String[]{String.valueOf(targetId)});
        database.close();
    }

    public void deleteTarget(int targetId){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete("target_table", "_id=?",
                new String[]{String.valueOf(targetId)});

        database.close();
    }

    public void updateTargetData(int id, String data) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues targetValues = new ContentValues();
        targetValues.put("data", data);
        database.update("target_table", targetValues, "_id=?",
                new String[]{String.valueOf(id)});
        database.close();
    }

    public ArrayList<Target> retrieveAllTargets() throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TARGET_TABLE_NAME,
                new String[]{"_id, name, url, primary_selector," +
                        "secondary_selector, group_selector," +
                        "data, enabled"}, null, null,
                null, null, null);
        cursor.moveToFirst();

        ArrayList<Target> targets = new ArrayList<>();

        if(cursor.getCount() ==0) return targets;

        do{
            Target target = new Target();
            target.setId(Integer.parseInt(cursor.getString(0)));
            target.setName(cursor.getString(1));
            target.setUrl(cursor.getString(2));
            target.setPrimarySelector(cursor.getString(3));
            target.setSecondarySelector(cursor.getString(4));
            target.setGroupSelector(cursor.getString(5));
            target.setCurrentData(cursor.getString(6));
            if(cursor.getString(7).equals("1")){
                target.setEnabled(true);
            }else target.setEnabled(false);
            targets.add(target);
        }while (cursor.moveToNext());

        cursor.close();
        database.close();
        return targets;
    }
}
