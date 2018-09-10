package com.randomappsinc.simpleflashcards.managers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.JSONUtils;

import java.io.File;
import java.util.List;

public class RestoreDataManager {

    public interface Listener {
        void onDataRestorationComplete();

        void onFileNotFound();
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

    @Nullable protected Listener listener;
    private Handler backgroundHandler;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private RestoreDataManager() {
        HandlerThread handlerThread = new HandlerThread("Restore Data");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void restoreData(String folderPath) {
        final File backupFile = new File(folderPath + "/" + BackupDataManager.BACKUP_FILE_NAME);
        if (backupFile.exists()) {
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    List<FlashcardSet> flashcardSets = JSONUtils.getSetsForDataRestoration(backupFile);
                    restoreFlashcardSets(flashcardSets);
                }
            });
        } else {
            if (listener != null) {
                listener.onFileNotFound();
            }
        }
    }

    protected void restoreFlashcardSets(final List<FlashcardSet> flashcardSets) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                DatabaseManager databaseManager = DatabaseManager.get();
                databaseManager.restoreFlashcardSets(flashcardSets);

                if (listener != null) {
                    listener.onDataRestorationComplete();
                }
            }
        });
    }
}
