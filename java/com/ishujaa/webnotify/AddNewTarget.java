package com.ishujaa.webnotify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddNewTarget extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_target);

        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra("update", false);
        int targetId = intent.getIntExtra("targetId", -1);

        SQLiteOpenHelper sqLiteOpenHelper = new DBHelper(this);

        EditText editTextName = findViewById(R.id.edit_text_name);
        EditText editTextURL = findViewById(R.id.edit_text_url);
        EditText editTextPrimarySelector = findViewById(R.id.edit_text_primary_selector);
        EditText editTextGroupSelector = findViewById(R.id.edit_text_group_selector);
        EditText editTextSecondarySelector = findViewById(R.id.edit_text_secondary_selector);
        EditText editTextData = findViewById(R.id.edit_text_data);

        CheckBox enableBox = findViewById(R.id.enabledCheck);

        Button insertButton = findViewById(R.id.insert_new_target);
        Button updateButton = findViewById(R.id.update_new_target);
        Button deleteButton = findViewById(R.id.delete_target);

        Button buttonOpenURL = findViewById(R.id.open_url_btn);
        buttonOpenURL.setOnClickListener(view -> {
            if(!editTextURL.getText().toString().isEmpty()){
                Intent intent1 = new Intent(AddNewTarget.this, WebV.class);
                intent1.putExtra("url", editTextURL.getText().toString());
                startActivity(intent1);
            }else{
                Toast.makeText(AddNewTarget.this, "NO URL", Toast.LENGTH_SHORT).show();
            }
        });

        if(isUpdate){

            try{
                SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
                Cursor cursor = database.query("target_table",
                        new String[]{"name, url, primary_selector," +
                                "secondary_selector, group_selector," +
                                "sleep, data, enabled"}, "_id=?",
                        new String[]{Integer.toString(targetId)},
                        null, null, null);

                cursor.moveToFirst();
                editTextName.setText(cursor.getString(0));
                editTextURL.setText(cursor.getString(1));
                editTextPrimarySelector.setText(cursor.getString(2));
                editTextSecondarySelector.setText(cursor.getString(3));
                editTextGroupSelector.setText(cursor.getString(4));
                editTextData.setText(cursor.getString(5));

                if(cursor.getString(6).equals("1")){
                    enableBox.setChecked(true);
                }else enableBox.setChecked(false);

                cursor.close();
                database.close();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            updateButton.setVisibility(View.VISIBLE);
            insertButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            editTextData.setVisibility(View.VISIBLE);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

                        ContentValues targetValues = new ContentValues();
                        targetValues.put("name", editTextName.getText().toString());
                        targetValues.put("url", editTextURL.getText().toString());
                        targetValues.put("primary_selector",
                                editTextPrimarySelector.getText().toString());
                        targetValues.put("secondary_selector",
                                editTextSecondarySelector.getText().toString());
                        targetValues.put("group_selector",
                                editTextGroupSelector.getText().toString());
                        targetValues.put("data", editTextData.getText().toString());
                        targetValues.put("enabled", enableBox.isChecked());

                        database.update("target_table", targetValues, "_id=?",
                                new String[]{String.valueOf(targetId)});
                        database.close();
                        Toast.makeText(view.getContext(), "Updated Successfully.",
                                Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        Toast.makeText(view.getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
                        database.delete("target_table", "_id=?",
                                new String[]{String.valueOf(targetId)});

                        database.close();
                        Toast.makeText(view.getContext(), "Deleted Successfully.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (Exception e){
                        Toast.makeText(view.getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{

            insertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
                        new DBHelper(view.getContext()).insertRecord(database,
                                editTextName.getText().toString(),
                                editTextURL.getText().toString(),
                                editTextPrimarySelector.getText().toString(),
                                editTextSecondarySelector.getText().toString(),
                                editTextGroupSelector.getText().toString(),
                                "initiated",
                                enableBox.isChecked());
                        database.close();
                        Toast.makeText(view.getContext(), "Inserted Successfully.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (Exception e){
                        Toast.makeText(view.getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }
}