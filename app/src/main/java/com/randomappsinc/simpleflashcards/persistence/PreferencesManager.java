package com.randomappsinc.simpleflashcards.persistence;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.simpleflashcards.utils.MyApplication;

public class PreferencesManager {

    private static final String FIRST_TIME_USER = "firstTimeUser";
    private static final String NUM_OPENS_KEY = "numOpens";

    private SharedPreferences prefs;
    private static PreferencesManager instance;

    public static PreferencesManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private PreferencesManager() {
        prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    }

    public boolean isFirstTimeUser() {
        return prefs.getBoolean(FIRST_TIME_USER, true);
    }

    public void rememberWelcome() {
        prefs.edit().putBoolean(FIRST_TIME_USER, false).apply();
    }

    public int getNumAppOpens() {
        return prefs.getInt(NUM_OPENS_KEY, 0);
    }

    public void logAppOpen() {
        prefs.edit().putInt(NUM_OPENS_KEY, getNumAppOpens() + 1).apply();
    }
}
