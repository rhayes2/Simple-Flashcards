package com.randomappsinc.simpleflashcards.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class PreferencesManager {

    private static final String FIRST_TIME_USER = "firstTimeUser";
    private static final String NUM_OPENS_KEY = "numOpens";
    private static final String NEARBY_NAME = "nearbyName";
    private static final String SHAKE_IS_NEW = "shakeIsNew";
    private static final String ENABLE_SHAKE = "enableShake";
    private static final String BACKUP_FOLDER_PATH = "backupFilePath";

    private static final int NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING = 5;
    private static final int NUM_APP_OPENS_BEFORE_ASKING_FOR_SHARE = 10;

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstTimeUser() {
        return prefs.getBoolean(FIRST_TIME_USER, true);
    }

    public void rememberWelcome() {
        prefs.edit().putBoolean(FIRST_TIME_USER, false).apply();
    }

    public void logAppOpen() {
        int currentOpens = prefs.getInt(NUM_OPENS_KEY, 0);
        prefs.edit().putInt(NUM_OPENS_KEY, ++currentOpens).apply();
    }

    public boolean shouldAskForRating() {
        int currentOpens = prefs.getInt(NUM_OPENS_KEY, 0);
        return currentOpens == NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING;
    }

    public boolean shouldAskForShare() {
        int currentOpens = prefs.getInt(NUM_OPENS_KEY, 0);
        return currentOpens == NUM_APP_OPENS_BEFORE_ASKING_FOR_SHARE;
    }

    public String getNearbyName() {
        return prefs.getString(NEARBY_NAME, "");
    }

    public void setNearbyName(String newName) {
        prefs.edit().putString(NEARBY_NAME, newName).apply();
    }

    public boolean shouldShowShakeAdvice() {
        boolean shouldShowShake = prefs.getBoolean(SHAKE_IS_NEW, true);
        prefs.edit().putBoolean(SHAKE_IS_NEW, false).apply();
        return shouldShowShake;
    }

    public boolean isShakeEnabled() {
        return prefs.getBoolean(ENABLE_SHAKE, true);
    }

    public void setShakeEnabled(boolean enableShake) {
        prefs.edit().putBoolean(ENABLE_SHAKE, enableShake).apply();
    }

    @Nullable
    public String getBackupFolderPath() {
        return prefs.getString(BACKUP_FOLDER_PATH, null);
    }

    public void setBackupFolderPath(String backupFolderPath) {
        prefs.edit().putString(BACKUP_FOLDER_PATH, backupFolderPath).apply();
    }
}
