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
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SharedPreferences myPreference;
    android.content.SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView tvSmall;
    TextView tvLarge;
    ImageView image;
    String url = "https://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    public static final int MAX_LINES = 15;
    private static final int SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING = 2;
    int numberentries = -1;
    int currententry = -1;
    JSONArray jarray;

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
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("listPref")) {
                    loadImage();
                }
            }
        };
        myPreference.registerOnSharedPreferenceChangeListener(listener);
        ConnectionCheck myCheck = new ConnectionCheck(this);
        if (myCheck.isNetworkReachable()) {


            //A common async task
            Download_JB myTask = new Download_JB(this);

            myTask.setnameValuePair("Name1","Value1");
            myTask.setnameValuePair("Name2","Value2");

            // //////////////////////////////////////////////////// demo this
            // telescoping initilization pattern
            //myTask.setnameValuePair("screen_name", "maddow").setnameValuePair("day", "today");
            // myTask.execute(MYURL);

            myTask.execute(url);
        }
        else
            Toast.makeText(this,"Uh Ohh cannot reach network",Toast.LENGTH_SHORT).show();
    }

    private void loadImage() {

    }
    public void processJSON(String string) {
        try {
            JSONObject jsonobject = new JSONObject(string);

            //*********************************
            //makes JSON indented, easier to read
            //Log.d(TAG,jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));
            //tvRaw.setText(jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));

            // you must know what the data format is, a bit brittle
            jarray = jsonobject.getJSONArray("pets");

            // how many entries
            numberentries = jarray.length();

            currententry = 0;
            //setJSONUI(currententry); // parse out object currententry

            //Log.i(TAG, "Number of entries " + numberentries);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }






}