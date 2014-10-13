package com.nashlincoln.blink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by nash on 10/12/14.
 */
public class BlinkReceiver extends BroadcastReceiver {
    private static final String TAG = "BlinkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        WifiManager manager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo connectionInfo = manager.getConnectionInfo();
        if (connectionInfo != null) {
            Log.d(TAG, "ssid: " + connectionInfo.getSSID());
        }
    }
}
