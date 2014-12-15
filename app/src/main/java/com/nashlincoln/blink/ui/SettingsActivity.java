package com.nashlincoln.blink.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.app.NetworkReceiver;
import com.nashlincoln.blink.network.BlinkApi;

/**
 * Created by nash on 10/18/14.
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = Fragment.instantiate(this, SettingsFragment.class.getName());
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.preference_key_host))) {
                String host = sharedPreferences.getString(key, "");
                BlinkApi.createService(host);
            }

            Intent intent = new Intent(getActivity(), NetworkReceiver.class);
            getActivity().sendBroadcast(intent);
        }
    }
}
