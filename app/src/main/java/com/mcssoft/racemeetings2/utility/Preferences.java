package com.mcssoft.racemeetings2.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.mcssoft.racemeetings2.R;

import java.util.Map;

public class Preferences {

    //<editor-fold defaultstate="collapsed" desc="Region: Access">
    private Preferences(Context context) {
        this.context = context;
        getPreferences();
    }

    public static synchronized Preferences getInstance(Context context) {
        if(!instanceExists()) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public static synchronized Preferences getInstance() {
        return instance;
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Keys/Values">
    public String getMeetingsShowTodayKey() {
        return Resources.getInstance().getString(R.string.pref_meetings_show_today_key);
    }

    public boolean getMeetingsShowToday() {
        return getDefaultSharedPreferences().getBoolean(getMeetingsShowTodayKey(), false);
    }

    public String getSaveMeetingsKey() {
        return Resources.getInstance().getString(R.string.pref_meetings_save_key);
    }

    public boolean getSaveMeetings() {
        return getDefaultSharedPreferences().getBoolean(getSaveMeetingsKey(), false);
    }
    //</editor-fold>

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void destroy() {
        context = null;
        instance = null;
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private Bundle getPreferences() {

        Map<String,?> prefsMap = getDefaultSharedPreferences().getAll();

        if(prefsMap.isEmpty()) {
            // No SharedPreferences set yet. App has probably been uninstalled then re-installed
            // and/or cache and data cleared. Set the app preferences defaults.
            PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
            prefsMap = getDefaultSharedPreferences().getAll();
        }

        Bundle prefsState = new Bundle();

        for (String key : prefsMap.keySet()) {
            Object obj = prefsMap.get(key);
            prefsState.putString(key, obj.toString());
        }

        return prefsState;
    }
    //</editor-fold>

    private Context context;
    private static volatile Preferences instance = null;
}
