package com.nashlincoln.blink.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.content.Syncro;

/**
 * Created by nash on 10/12/14.
 */
public class BlinkReceiver extends BroadcastReceiver {
    private static final String TAG = "BlinkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean connected = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            String ssid = connectionInfo.getSSID();
            ssid = ssid.replaceAll("\"", "");
            Log.d(TAG, "ssid: " + ssid + " " + PreferenceUtils.getString(R.string.preference_key_ssid));

            connected = ssid.equals(PreferenceUtils.getString(R.string.preference_key_ssid));
        }

        Syncro.getInstance().onConnected(connected);
    }
}
