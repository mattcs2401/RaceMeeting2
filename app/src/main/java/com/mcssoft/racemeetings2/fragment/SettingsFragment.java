package com.mcssoft.racemeetings2.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.utility.Resources;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_main, container, false);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listener">
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String bp = "";
    }
    //</editor-fold>

    /**
     * Get the shared preference associated with showing today's Meetings on app start.
     * @return The preference value.
     */
    public boolean getShowToday() {
        return sharedPreferences.getBoolean(Resources.getInstance()
                .getString(R.string.pref_meetings_show_today_key), true);
    }

    /**
     * Get the shared preference associated with saving downloaded Meetings.
     * @return The preference value.
     */
    public boolean getSaveMeetings() {
        return sharedPreferences.getBoolean(Resources.getInstance()
                .getString(R.string.pref_meetings_save_key), true);
    }

    private SharedPreferences sharedPreferences;
}