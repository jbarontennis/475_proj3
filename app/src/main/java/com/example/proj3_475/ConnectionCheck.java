package com.example.proj3_475;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class ConnectionCheck {



    /**
     * @author lynn
     */
        Context context;

        ConnectionCheck(Context context) {
            this.context = context;
        }

        public boolean isNetworkReachable() {
            ConnectivityManager mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo current = mManager.getActiveNetworkInfo();
            if (current == null) {
                return false;
            }
            return (current.getState() == NetworkInfo.State.CONNECTED);
        }

        /***
         *
         * @return
         */
        public boolean isWifiReachable() {
            ConnectivityManager mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo current = mManager.getActiveNetworkInfo();
            if (current == null) {
                return false;
            }
            return (current.getType() == ConnectivityManager.TYPE_WIFI);
        }
    }

