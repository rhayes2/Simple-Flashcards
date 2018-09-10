package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.JSONUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class BackupDataManager {

    public static final String BACKUP_FILE_NAME = "simple-flashcards-plus-backup.txt";

    public interface Listener {
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
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private DatabaseManager databaseManager = DatabaseManager.get();
    @Nullable protected Listener listener;

    private BackupDataManager() {
        HandlerThread handlerThread = new HandlerThread("Backup Data");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void backupData(final Context context, final boolean userTriggered) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        final String backupFolderPath = preferencesManager.getBackupFolderPath();
        if (backupFolderPath == null) {
            return;
        }

        final List<FlashcardSet> flashcardSets = databaseManager.getAllFlashcardSetsClean();
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                File file = new File(backupFolderPath, BACKUP_FILE_NAME);
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    stream.write(JSONUtils.serializeFlashcardSets(flashcardSets).getBytes());
                    stream.close();
                    if (userTriggered) {
                        alertListenerOfBackupComplete();
                    }
                } catch (Exception exception) {
                    if (userTriggered) {
                        alertListenerOfBackupFail();
                    }
                }
            }
        });
    }

    protected void alertListenerOfBackupComplete() {
        if (listener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onBackupComplete();
                }
            });
        }
    }

    protected void alertListenerOfBackupFail() {
        if (listener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onBackupFailed();
                }
            });
        }
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }
}
