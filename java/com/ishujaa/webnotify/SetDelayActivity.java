package com.ishujaa.webnotify;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetDelayActivity extends AppCompatActivity {

    private SharedPrefHelper sharedPrefHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delay);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView currentVal = findViewById(R.id.text_view_current_val);
        sharedPrefHelper = new SharedPrefHelper(this);
        currentVal.setText("Current Val: "+String.valueOf(sharedPrefHelper.getDelay()));
    }

    public void btnUpdateDelayClick(View view) {
        EditText editTextNewVal = findViewById(R.id.edit_text_new_val);

        if(editTextNewVal.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a value.", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            long newMillis = Integer.parseInt(editTextNewVal.getText().toString());
            sharedPrefHelper.setDelay(newMillis);
            Toast.makeText(this, "Successfully updated.", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}