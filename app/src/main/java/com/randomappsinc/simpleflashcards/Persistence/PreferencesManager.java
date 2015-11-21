package com.randomappsinc.simpleflashcards.Persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.simpleflashcards.Utils.MyApplication;

/**
 * Created by alexanderchiou on 11/20/15.
 */
public class PreferencesManager {
    private static final String FIRST_TIME_USER = "firstTimeUser";

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
        Context context = MyApplication.get().getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstTimeUser() {
        return prefs.getBoolean(FIRST_TIME_USER, true);
    }

    public void rememberWelcome() {
        prefs.edit().putBoolean(FIRST_TIME_USER, false).apply();
    }
}
