package com.randomappsinc.simpleflashcards.utils;

import android.support.annotation.StringRes;

public class StringUtils {

    public static String getString(@StringRes int resId) {
        return MyApplication.getAppContext().getString(resId);
    }
}
