package com.example.proj3_475;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences myPreference;
    android.content.SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView tvSmall;
    TextView tvLarge;
    ImageView image;
    String url = "https://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        tvLarge = findViewById(R.id.tvLarge);
        tvSmall = findViewById(R.id.tvSmall);
        image = findViewById(R.id.imageView1);
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
        if(isNetworkReachable() && isWifiReachable()){

        }
    }


    public boolean isNetworkReachable() {
        NetworkInfo current = getNetworkInfo();
        return (current == null)?false:(current.getState() == NetworkInfo.State.CONNECTED);
    }

    public boolean isWifiReachable() {
        NetworkInfo current = getNetworkInfo();
        return (current == null)?false:(current.getType() == ConnectivityManager.TYPE_WIFI);
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager mManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return mManager.getActiveNetworkInfo();
    }
    private class Download extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}