package com.example.proj3_475;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences myPreference;
    android.content.SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView tvSmall;
    TextView tvLarge;
    ImageView image;
    List<Pet> petList  = new ArrayList<>();
    Spinner spinner;
    ArrayAdapter<String> adapter;
    String prefurl = "pets.json";
    String url = "https://www.pcs.cnu.edu/~kperkins/pets/";
    public static final int MAX_LINES = 15;
    private static final int SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING = 2;
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
        getSupportActionBar().hide();
        tvLarge = findViewById(R.id.tvLarge);
        tvSmall = findViewById(R.id.tvSmall);
        image = findViewById(R.id.imageView1);
        spinner = (Spinner)findViewById(R.id.spinner);

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
            Download myTask = new Download(prefurl);


            // //////////////////////////////////////////////////// demo this
            // telescoping initilization pattern
            //myTask.setnameValuePair("screen_name", "maddow").setnameValuePair("day", "today");
            // myTask.execute(MYURL);

            myTask.execute(url);
        }
        else
            Toast.makeText(this,"Uh Ohh cannot reach network",Toast.LENGTH_SHORT).show();
    }

    public void setUpSpinner(){
        String []listNames = new String[petList.size()];
        for(int i = 0;i<petList.size();i++){
            listNames[i] = petList.get(i).name;
        }
        spinner.setVisibility(View.VISIBLE);
        adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout,listNames );
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public static final int SELECTED_ITEM = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getChildAt(SELECTED_ITEM) != null){
                    // ((TextView) parent.getChildAt(SELECTED_ITEM).setTextColor(Color.WHITE);
                    Toast.makeText(MainActivity.this, (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void loadImage() {

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
            // how many entries
            numberentries = jarray.length();

            currententry = 0;
            //setJSONUI(currententry); // parse out object currententry

            //Log.i(TAG, "Number of entries " + numberentries);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class Download extends AsyncTask<String, Void, String> {
        private static final String     TAG = "Download";
        private static final int        TIMEOUT = 1000;    // 1 second
        private String                  myQuery = "";
        protected int                   statusCode = 0;
        protected String                myURL;
        String data;
        public Download(String url){
            myURL = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // site we want to connect to
            myURL = params[0];

            try {
                URL url = new URL( myURL + prefurl);

                // this does no network IO
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // can further configure connection before getting data
                // cannot do this after connected
               /* connection.setRequestMethod("GET");
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");*/

                // wrap in finally so that stream bis is sure to close
                // and we disconnect the HttpURLConnection
                BufferedReader in = null;
                try {

                    // this opens a connection, then sends GET & headers
                    connection.connect();

                    // lets see what we got make sure its one of
                    // the 200 codes (there can be 100 of them
                    // http_status / 100 != 2 does integer div any 200 code will = 2
                    statusCode = connection.getResponseCode();
                    if (statusCode / 100 != 2) {
                        Log.e(TAG, "Error-connection.getResponseCode returned "
                                + Integer.toString(statusCode));
                        return null;
                    }

                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8096);

                    // the following buffer will grow as needed
                    String myData;
                    StringBuffer sb = new StringBuffer();

                    while ((myData = in.readLine()) != null) {
                        sb.append(myData);
                    }
                    return sb.toString();

                } finally {
                    // close resource no matter what exception occurs
                    if(in != null) {
                        in.close();
                    }
                    connection.disconnect();
                }
            } catch (Exception exc) {
                return null;
            }
        }

        /**
         *
         * @param result null if failure or text of the html page
         *               override this method in subclass to customize it to calling app
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            processJSON(result);
            setUpSpinner();
        }

        /**
         * not implemented above, once you start the download you are in it for the long haul
         * Not a good idea, what if its a giant file?
         * override this method in subclass to customize it to calling app
         */
        @Override
        protected void onCancelled() {
            //override to handle this
            super.onCancelled();
        }
    };




}