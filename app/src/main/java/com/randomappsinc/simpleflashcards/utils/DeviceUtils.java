package com.randomappsinc.simpleflashcards.utils;

import android.os.Build;

public class DeviceUtils {

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalizeIfNecessary(model);
        } else {
            return capitalizeIfNecessary(manufacturer) + " " + model;
        }
    }


    private static String capitalizeIfNecessary(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        char first = input.charAt(0);
        if (Character.isUpperCase(first)) {
            return input;
        } else {
            return Character.toUpperCase(first) + input.substring(1);
        }
    }
}
