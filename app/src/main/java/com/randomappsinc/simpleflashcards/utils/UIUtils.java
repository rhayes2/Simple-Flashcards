package com.randomappsinc.simpleflashcards.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;

public class UIUtils {

    private static final int NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING = 5;

    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
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

    public static void showLongToast(@StringRes int stringId) {
        showToast(stringId, Toast.LENGTH_LONG);
    }

    private static void showToast(@StringRes int stringId, int duration) {
        Toast.makeText(MyApplication.getAppContext(), stringId, duration).show();
    }

    public static void askForRatingIfAppropriate(final Activity activity) {
        if (PreferencesManager.get().getNumAppOpens() == NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING) {
            new MaterialDialog.Builder(activity)
                    .content(R.string.please_rate)
                    .negativeText(R.string.no_im_good)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(activity
                                    .getPackageManager()
                                    .queryIntentActivities(intent, 0).size() > 0)) {
                                UIUtils.showToast(R.string.play_store_error, Toast.LENGTH_LONG);
                                return;
                            }
                            activity.startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}
