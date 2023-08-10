package com.ishujaa.webnotify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBC {
    private Context context;
    public String TARGET_TABLE_NAME = "target_table";
    public String LOG_TABLE_NAME = "log_table";
    SQLiteOpenHelper sqLiteOpenHelper;
    DBC(Context context){
        this.context = context;
        sqLiteOpenHelper = new DBHelper(context);
    }

    public String readTargetData(int id) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TARGET_TABLE_NAME, new String[]{"data"},
                "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        String data = cursor.getString(0);
        cursor.close();
        database.close();
        return data;
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
