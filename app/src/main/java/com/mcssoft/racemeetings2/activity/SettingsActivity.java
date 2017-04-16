package com.mcssoft.racemeetings2.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.mcssoft.racemeetings2.R;

public class SettingsActivity extends PreferenceActivity
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        protected void onCreate(Bundle savedState) {
            super.onCreate(savedState);
            setContentView(R.layout.settings_main);
            addPreferencesFromResource(R.xml.preferences);
//            getFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, new SettingsFragment())
//                    .commit();
        }

        @Override
        protected void onResume() {
            super.onResume();

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        protected void onPause() {
            super.onPause();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String bp = "";
//        if (key.equals(SOME_KEY)) {
//            // do something.
//        }
    }

    private SharedPreferences sharedPreferences;
}
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_main, container, false);
        addPreferencesFromResource(R.xml.preferences);
        return rootView;
    }
*/

