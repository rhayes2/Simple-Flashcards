package com.randomappsinc.simpleflashcards.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderchiou on 11/20/15.
 */
public class MiscUtils {
    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSnackbar(View parent, String message, int length) {
        Context context = MyApplication.get().getApplicationContext();
        Snackbar snackbar = Snackbar.make(parent, message, length);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_blue));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static int getRandomFlashcardPosition(int numFlashcards, int currentPosition) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < numFlashcards; i++) {
            if (i != currentPosition) {
                positions.add(i);
            }
        }
        Collections.shuffle(positions);
        return positions.get(0);
    }
}
