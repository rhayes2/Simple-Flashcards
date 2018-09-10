package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;

import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.JSONUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class BackupDataManager {

    private static final String BACKUP_FILE_NAME = "simple-flashcards-plus-backup.txt";

    public interface Listener {
        void onBackupStarted();

        void onBackupComplete();

        void onBackupFailed();
    }

    private static BackupDataManager instance;

    public static BackupDataManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized BackupDataManager getSync() {
        if (instance == null) {
            instance = new BackupDataManager();
        }
        return instance;
    }

    private Handler backgroundHandler;
    private DatabaseManager databaseManager = DatabaseManager.get();
    @Nullable protected Listener listener;

    private BackupDataManager() {
        HandlerThread handlerThread = new HandlerThread("Backup Data");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void backupData(final Context context) {
        if (listener != null) {
            listener.onBackupStarted();
        }

        final List<FlashcardSet> flashcardSets = databaseManager.getAllFlashcardSetsClean();
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                PreferencesManager preferencesManager = new PreferencesManager(context);
                File file = new File(preferencesManager.getBackupFolderPath(), BACKUP_FILE_NAME);
                Log.d("Backup", file.getAbsolutePath());
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    stream.write(JSONUtils.serializeFlashcardSets(flashcardSets).getBytes());
                    stream.close();

                    if (listener != null) {
                        listener.onBackupComplete();
                    }
                } catch (Exception exception) {
                    if (listener != null) {
                        listener.onBackupFailed();
                    }
                }
            }
        });
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }
}
