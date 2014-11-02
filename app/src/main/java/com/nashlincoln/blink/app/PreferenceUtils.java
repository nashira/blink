package com.nashlincoln.blink.app;

/**
 * Created by nash on 11/1/14.
 */
public class PreferenceUtils {

    public static String getString(int prefKeyId) {
        final String prefKey = BlinkApp.getApp().getString(prefKeyId);
        return BlinkApp.getPreferences().getString(prefKey, null);
    }
}
