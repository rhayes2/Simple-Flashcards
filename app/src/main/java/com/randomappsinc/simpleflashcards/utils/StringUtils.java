package com.randomappsinc.simpleflashcards.utils;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

public class StringUtils {

    public static String getString(@StringRes int resId) {
        return MyApplication.getAppContext().getString(resId);
    }

    public static String getSaneDeviceString(String endpointName) {
        int newlinePos = endpointName.indexOf("\n");
        if (newlinePos == -1 || newlinePos == endpointName.length() - 1) {
            return endpointName;
        } else {
            String nearbyName = endpointName.substring(0, newlinePos);
            String deviceType = endpointName.substring(newlinePos + 1);
            return nearbyName + " (" + deviceType + ")";
        }
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        return MyApplication.getAppContext().getResources().getStringArray(resId);
    }
}
