package com.nashlincoln.blink.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.nashlincoln.blink.BuildConfig;
import com.nashlincoln.blink.R;
import com.nashlincoln.blink.model.DaoMaster;
import com.nashlincoln.blink.model.DaoSession;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.network.BlinkApi;

/**
 * Created by nash on 10/17/14.
 */
public class BlinkApp extends Application {
    public static final String EXTRA_NFC_WRITE = BuildConfig.APPLICATION_ID + ".NFC_WRITE";
    private static String PREF_API_HOST;
    private static BlinkApp sInstance;

    private SharedPreferences mPreferences;
    private DaoSession mDaoSession;

    public static BlinkApp getApp() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PREF_API_HOST = getString(R.string.preference_key_host);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "blink-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sInstance.mDaoSession;
    }

    private void fetchData() {
        if (isConfigured()) {
            Syncro.getInstance().fetchAttributeTypes();
            Syncro.getInstance().fetchDeviceTypes();
            Syncro.getInstance().fetchDevices();
        }
    }

    public boolean isConfigured() {
        return !getHost().equals("");
    }

    public void setHost(String host) {
        BlinkApi.createService(host);
        fetchData();
    }

    public String getHost() {
        return mPreferences.getString(PREF_API_HOST, "");
    }

    public static SharedPreferences getPreferences() {
        return getApp().mPreferences;
    }
}
