package com.ishujaa.webnotify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddTargetActivity extends AppCompatActivity {

    private DBAccess DBAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_target);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra("update", false);
        int targetId = intent.getIntExtra("targetId", -1);

        //SQLiteOpenHelper sqLiteOpenHelper = new DBHelper(this);

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
                Intent intent1 = new Intent(AddTargetActivity.this, WebViewActivity.class);
                intent1.putExtra("url", editTextURL.getText().toString());
                startActivity(intent1);
            }else{
                Toast.makeText(AddTargetActivity.this, "NO URL", Toast.LENGTH_SHORT).show();
            }
        });

        DBAccess = new DBAccess(this);

        if(isUpdate){

            try{

                Target target = DBAccess.getTargetFields(targetId);

                editTextName.setText(target.getName());
                editTextURL.setText(target.getUrl());
                editTextPrimarySelector.setText(target.getPrimarySelector());
                editTextSecondarySelector.setText(target.getSecondarySelector());
                editTextGroupSelector.setText(target.getGroupSelector());
                editTextData.setText(target.getCurrentData());

                enableBox.setChecked(target.isEnabled());

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
                        DBAccess.updateTargetFields(editTextName.getText().toString(),
                                editTextURL.getText().toString(),
                                editTextPrimarySelector.getText().toString(), editTextSecondarySelector.getText().toString(),
                                editTextGroupSelector.getText().toString(), editTextData.getText().toString(),
                                enableBox.isChecked(), targetId);
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
                        DBAccess.deleteTarget(targetId);
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

                    if(editTextName.getText().toString().isEmpty() || editTextURL.getText().toString().isEmpty()||
                    editTextPrimarySelector.getText().toString().isEmpty() ||
                            editTextSecondarySelector.getText().toString().isEmpty()){
                        Toast.makeText(view.getContext(),
                                "Please enter required fields.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try{
                        DBAccess.insertTarget(
                                editTextName.getText().toString(),
                                editTextURL.getText().toString(),
                                editTextPrimarySelector.getText().toString(),
                                editTextSecondarySelector.getText().toString(),
                                editTextGroupSelector.getText().toString(),
                                "initiated",
                                enableBox.isChecked());

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