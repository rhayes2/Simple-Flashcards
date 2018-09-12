package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class RestoreDataManager {

    public interface Listener {
        void onDataRestorationComplete(int[] addedSetIds);

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

    public void restoreDataFromFolderPath(String folderPath) {
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

    public void restoreDataFromUri(final Uri uri, final Context context) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    if (inputStream == null) {
                        alertOfFileNotFound();
                        return;
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                    String setsJson = stringBuilder.toString();
                    List<FlashcardSet> flashcardSets = JSONUtils.deserializeSets(setsJson);
                    restoreFlashcardSets(flashcardSets);
                } catch (Exception exception) {
                    alertOfFileNotFound();
                }
            }
        });
    }

    protected void alertOfFileNotFound() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFileNotFound();
                }
            }
        });
    }

    protected void restoreFlashcardSets(final List<FlashcardSet> flashcardSets) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                DatabaseManager databaseManager = DatabaseManager.get();
                int[] addedSetIds = databaseManager.restoreFlashcardSets(flashcardSets);

                if (listener != null) {
                    listener.onDataRestorationComplete(addedSetIds);
                }
            }
        });
    }
}
