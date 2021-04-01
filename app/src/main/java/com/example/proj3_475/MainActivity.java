package com.example.proj3_475;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences myPreference;
    android.content.SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView tvSmall;
    TextView tvLarge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvLarge = findViewById(R.id.tvLarge);
        tvSmall = findViewById(R.id.tvSmall);
        myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                if(key.equals("listPref")){
                    loadImage();
                }
            }
        };
        myPreference.registerOnSharedPreferenceChangeListener(listener);
    }

    private void loadImage() {
    }
}