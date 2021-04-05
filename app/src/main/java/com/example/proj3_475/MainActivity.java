package com.example.proj3_475;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    SharedPreferences myPreference;
    android.content.SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView tvSmall;
    TextView tvLarge;
    ImageView image;
    List<Pet> petList  = new ArrayList<>();
    Spinner spinner;
    String prefurl = "pets.json";
    String url = "https://www.pcs.cnu.edu/~kperkins/pets/";
    int numberentries = -1;
    int currententry = -1;
    JSONArray jarray;
    class Pet{
        public String name;
        public String file;
        public Pet(String name, String file){
            this.name = name;
            this.file = file;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvLarge = findViewById(R.id.tvLarge);
        tvSmall = findViewById(R.id.tvSmall);
        image = findViewById(R.id.imageView1);

        ConnectionCheck myCheck = new ConnectionCheck(this);

        myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("prefURL")) {
                    url = myPreference.getString(key, getString(R.string.URLpet));
                    spinner.setVisibility(View.VISIBLE);
                    if(myCheck.isNetworkReachable()) {
                    setUpSpinner();
                        //loadImage();
                    }else{

                        tvLarge.setText(getString(R.string.unreachable));
                        image.setImageResource(R.drawable.errorback);
                        tvLarge.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        myPreference.registerOnSharedPreferenceChangeListener(listener);
        if (myCheck.isNetworkReachable()) {
            Download myTask = new Download(prefurl);
            myTask.execute(url);
        }
        else
            tvLarge.setText("0!");
        tvSmall.setText("for " + url);
        image.setImageResource(R.drawable.errorback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menu) {
        if (menu.getItemId() == R.id.menu) {
            startActivity(new Intent(this, PreferenceSettings.class));
        }
        return super.onOptionsItemSelected(menu);
    }
    public void setUpSpinner(){
        String []listNames = new String[petList.size()];
        for(int i = 0;i<petList.size();i++){
            listNames[i] = petList.get(i).name;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listNames );
        spinner = (Spinner)findViewById(R.id.spin);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public static final int SELECTED_ITEM = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getChildAt(SELECTED_ITEM) != null){
                    Toast.makeText(MainActivity.this, (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    loadImage(petList.get(position).file);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void loadImage(String pic) {
        String extendedUrl = url + pic;
        DownloadImage dImage = new DownloadImage();
        try {
            Bitmap bit = dImage.execute(extendedUrl).get();
            image.setImageBitmap(bit);
        }catch(Exception e){

        }
    }
    public void processJSON(String string) {
        try {
            JSONObject jsonobject = new JSONObject(string);
            String petName = "";
            String fileName = "";

            jarray = jsonobject.getJSONArray("pets");
            for(int i = 0;i< jarray.length(); i++){
                JSONObject jobj = jarray.getJSONObject(i);
                petName = jobj.get("name") + "";
                fileName = jobj.get("file") + "";
                Pet tmp = new Pet(petName, fileName);
                petList.add(tmp);
            }
            numberentries = jarray.length();

            currententry = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class Download extends AsyncTask<String, Void, String> {
        private static final String     TAG = "Download";
        private String                  myQuery = "";
        protected int                   statusCode = 0;
        protected String                myURL;
        String data;
        public Download(String url){
            myURL = url;
        }

        @Override
        protected String doInBackground(String... params) {
            myURL = params[0];
            try {
                URL url = new URL( myURL + prefurl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader in = null;
                try {
                    connection.connect();

                    statusCode = connection.getResponseCode();
                    if (statusCode / 100 != 2) {
                        Log.e(TAG, "Error-connection.getResponseCode returned "
                                + Integer.toString(statusCode));
                        return null;
                    }

                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8096);
                    String myData;
                    StringBuffer sb = new StringBuffer();

                    while ((myData = in.readLine()) != null) {
                        sb.append(myData);
                    }
                    return sb.toString();

                } finally {
                    if(in != null) {
                        in.close();
                    }
                    connection.disconnect();
                }
            } catch (Exception exc) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            processJSON(result);
            setUpSpinner();
            loadImage("p0.png");
        }

        @Override
        protected void onCancelled() {
            //override to handle this
            super.onCancelled();
        }
    };
private class DownloadImage extends AsyncTask<String, Void, Bitmap>{
    Bitmap bitmap = null;

    @Override
    protected Bitmap doInBackground(String... strings) {

        URL url;
        HttpURLConnection connect;
        InputStream in;
        try{
            url = new URL(strings[0]);
            connect = (HttpURLConnection) url.openConnection();
            in = connect.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap result){
        super.onPostExecute(result);
        if(bitmap != null) {
            tvLarge.setVisibility(View.INVISIBLE);
            tvSmall.setVisibility(View.INVISIBLE);
            image.setImageBitmap(result);
        }else{
            tvLarge.setVisibility(View.VISIBLE);
            tvSmall.setVisibility(View.VISIBLE);
            tvLarge.setText("404!");
            tvSmall.setText(getString(R.string.text2)+ " " + url);
            spinner.setVisibility(View.INVISIBLE);
            image.setImageResource(R.drawable.errorback);
        }
    }
}




}