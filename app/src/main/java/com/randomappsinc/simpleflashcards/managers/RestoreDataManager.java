package com.randomappsinc.simpleflashcards.managers;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import java.io.File;

public class RestoreDataManager {

    public interface Listener {
        void onDataRestorationComplete();

        void onFileNotFound();

        void onDataRestorationFailure();
    }

    private static RestoreDataManager instance;

    public static RestoreDataManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized RestoreDataManager getSync() {
        if (instance == null) {
            instance = new RestoreDataManager();
        }
        return instance;
    }

    @Nullable private Listener listener;
    private Handler backgroundHandler;

    private RestoreDataManager() {
        HandlerThread handlerThread = new HandlerThread("Restore Data");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void restoreData(String folderPath) {
        File backupFile = new File(folderPath + "/" + BackupDataManager.BACKUP_FILE_NAME);
        if (backupFile.exists()) {

        } else {
            if (listener != null) {
                listener.onFileNotFound();
            }
        }
    }
}
