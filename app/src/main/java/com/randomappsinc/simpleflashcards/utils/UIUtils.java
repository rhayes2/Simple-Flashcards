package com.randomappsinc.simpleflashcards.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.simpleflashcards.R;

public class UIUtils {

    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSnackbar(View parent, String message, int length) {
        Context context = MyApplication.getAppContext();
        Snackbar snackbar = Snackbar.make(parent, message, length);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_blue));
        TextView tv = rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showShortToast(@StringRes int stringId) {
        showToast(stringId, Toast.LENGTH_SHORT);
    }

    private static void showToast(@StringRes int stringId, int duration) {
        Toast.makeText(MyApplication.getAppContext(), stringId, duration).show();
    }
}
