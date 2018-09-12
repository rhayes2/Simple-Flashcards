package com.randomappsinc.simpleflashcards.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

public class FileUtils {

    @Nullable
    public static File writeFlashcardSetToFile(FlashcardSet flashcardSet, Context context) {
        String filename = String.valueOf(flashcardSet.getId());
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

    public static String getFileContents(File file) {
        Scanner scanner = null;
        String contents = "";
        try {
            scanner = new Scanner(file);
            contents = scanner.useDelimiter("\\A").next();
        }
        catch (FileNotFoundException ignored) {}
        finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return contents;
    }
}
