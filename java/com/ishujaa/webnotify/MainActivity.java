package com.ishujaa.webnotify;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView targetListView;
    private TextView serviceStatus;
    private TextView lastUpdateView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_new) {
            startActivity(new Intent(this, AddNewTarget.class));
            return true;
        }else if(id == R.id.action_set_delay){

        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},  1);
        }

        lastUpdateView = findViewById(R.id.last_update_view);
        targetListView = findViewById(R.id.target_list_view);

        targetListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(view.getContext(), AddNewTarget.class);
            intent.putExtra("targetId", (int) l);
            intent.putExtra("update", true);
            startActivity(intent);

        });

        Button buttonStartService = findViewById(R.id.btn_start_service);
        buttonStartService.setOnClickListener(view -> {
            startService(new Intent(view.getContext(), MyWorkerService.class));
            setServiceLabel();
        });
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyWorkerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void stopWorkerService(){
        stopService(new Intent(this, MyWorkerService.class));
    }

    private void setServiceLabel(){
        serviceStatus = findViewById(R.id.service_status);

        if(isMyServiceRunning()){
            serviceStatus.setText("Notifier Service Running.");
            serviceStatus.setTextColor(Color.GREEN);
        }else{
            serviceStatus.setText("Service not Running.");
            serviceStatus.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            DBAccess DBAccess = new DBAccess(this);
            Cursor cursor = DBAccess.getTargetNamesCursor();

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
            targetListView.setAdapter(cursorAdapter);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        setServiceLabel();

        String lastUpdate = "Last Updated: ";
        try {
            lastUpdate += new SPHelper(this).getLastUpdateTime();;
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        lastUpdateView.setText(lastUpdate);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}