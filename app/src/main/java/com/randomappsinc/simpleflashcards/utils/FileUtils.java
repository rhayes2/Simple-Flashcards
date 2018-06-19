package com.randomappsinc.simpleflashcards.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {

    @Nullable
    public static File writeFlashcardSetToFile(FlashcardSet flashcardSet) {
        String filename = String.valueOf(flashcardSet);
        Context context = MyApplication.getAppContext();
        File file = new File(context.getFilesDir(), filename);
        String fileContents = JSONUtils.serializeFlashcardSet(flashcardSet);

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFileForFlashcardSet(FlashcardSet flashcardSet) {
        File file = new File(
                MyApplication.getAppContext().getFilesDir(),
                String.valueOf(flashcardSet.getId()));
        file.delete();
    }
}
