package com.ishujaa.webnotify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebV extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_v);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        if(url != null){
            WebView webView = findViewById(R.id.webView);
            webView.loadUrl(url);
            webView.getSettings().setJavaScriptEnabled(true);
        }else{
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}