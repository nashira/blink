package com.nashlincoln.blink.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.nashlincoln.blink.R;

/**
 * Created by nash on 10/18/14.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);

        if (savedInstanceState == null) {
            Fragment fragment = Fragment.instantiate(this, SettingsFragment.class.getName());
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals(getString(R.string.preference_key_host))) {
                BlinkApp.getApp().setHost(((EditTextPreference)preference).getText());
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}
