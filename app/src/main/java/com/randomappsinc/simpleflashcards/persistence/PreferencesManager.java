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
    private static final String SHOULD_SHOW_FLASHCARD_SET_INSTRUCTIONS = "shouldShowFlashcardSetInstructions";

    // Pre-Kitkat (19), we work with file paths
    private static final String BACKUP_FILE_PATH = "backupFilePath";

    // On KitKat+, we work with URIs (Storage Access Framework)
    private static final String BACKUP_URI = "backupUri";

    private static final String LAST_BACKUP_TIME = "lastBackupTime";

    private static final String RATING_DIALOG_SEEN = "ratingDialogSeen";
    private static final String SHARING_DIALOG_SEEN = "sharingDialogSeen";
    private static final String BACKUP_DATA_DIALOG_SEEN = "backupDataDialogSeen";

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
        boolean ratingDialogSeen = prefs.getBoolean(RATING_DIALOG_SEEN, false);
        return currentOpens >= NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING && !ratingDialogSeen;
    }

    public void rememberRatingDialogSeen() {
        prefs.edit().putBoolean(RATING_DIALOG_SEEN, true).apply();
    }

    public boolean shouldAskForShare() {
        int currentOpens = prefs.getInt(NUM_OPENS_KEY, 0);
        boolean sharingDialogSeen = prefs.getBoolean(SHARING_DIALOG_SEEN, false);
        return currentOpens >= NUM_APP_OPENS_BEFORE_ASKING_FOR_SHARE && !sharingDialogSeen;
    }

    public void rememberSharingDialogSeen() {
        prefs.edit().putBoolean(SHARING_DIALOG_SEEN, true).apply();
    }

    public boolean hasSeenBackupDataDialog() {
        return prefs.getBoolean(BACKUP_DATA_DIALOG_SEEN, false);
    }

    public void rememberBackupDataDialogSeen() {
        prefs.edit().putBoolean(BACKUP_DATA_DIALOG_SEEN, true).apply();
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
    public String getBackupFilePath() {
        return prefs.getString(BACKUP_FILE_PATH, null);
    }

    public void setBackupFilePath(@Nullable String backupFilePath) {
        prefs.edit().putString(BACKUP_FILE_PATH, backupFilePath).apply();
    }

    @Nullable
    public String getBackupUri() {
        return prefs.getString(BACKUP_URI, null);
    }

    public void setBackupUri(@Nullable String uriString) {
        prefs.edit().putString(BACKUP_URI, uriString).apply();
    }

    public long getLastBackupTime() {
        return prefs.getLong(LAST_BACKUP_TIME, 0);
    }

    public void updateLastBackupTime() {
        prefs.edit().putLong(LAST_BACKUP_TIME, System.currentTimeMillis()).apply();
    }

    public boolean shouldShowRenameFlashcardSetInstructions() {
        boolean shouldShowShake = prefs.getBoolean(SHOULD_SHOW_FLASHCARD_SET_INSTRUCTIONS, true);
        prefs.edit().putBoolean(SHOULD_SHOW_FLASHCARD_SET_INSTRUCTIONS, false).apply();
        return shouldShowShake;
    }
}
