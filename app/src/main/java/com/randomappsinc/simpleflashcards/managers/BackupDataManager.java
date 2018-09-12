package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
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

    public void setBackupLocation(String folderPath, Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        preferencesManager.setBackupFilePath(folderPath + "/" + BACKUP_FILE_NAME);
        backupData(context, true);
    }

    public void backupData(final Context context, final boolean userTriggered) {
        final PreferencesManager preferencesManager = new PreferencesManager(context);
        final List<FlashcardSet> flashcardSets = databaseManager.getAllFlashcardSetsClean();

        // Try the File IO strategy (should only apply on pre-KitKat devices)
        final String backupFolderPath = preferencesManager.getBackupFilePath();
        if (backupFolderPath != null) {
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    File file = new File(backupFolderPath);
                    try {
                        FileOutputStream stream = new FileOutputStream(file);
                        stream.write(JSONUtils.serializeFlashcardSets(flashcardSets).getBytes());
                        stream.close();
                        preferencesManager.updateLastBackupTime();
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
        // Try the Storage Access Framework URI strategy for KitKat+
        else if (preferencesManager.getBackupUri() != null){
            final String backupUri = preferencesManager.getBackupUri();
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        ParcelFileDescriptor fileDescriptor = context.getContentResolver().
                                openFileDescriptor(Uri.parse(backupUri), "w");
                        if (fileDescriptor == null) {
                            if (userTriggered) {
                                alertListenerOfBackupFail();
                            }
                            return;
                        }
                        FileOutputStream fileOutputStream =
                                new FileOutputStream(fileDescriptor.getFileDescriptor());
                        fileOutputStream.write(JSONUtils.serializeFlashcardSets(flashcardSets).getBytes());
                        fileOutputStream.close();
                        fileDescriptor.close();
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
        // If we have no backup paths to write to and we called this method, something's wrong
        else {
            if (userTriggered) {
                alertListenerOfBackupFail();
            }
        }
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
