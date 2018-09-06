package com.randomappsinc.simpleflashcards.utils;

public class StringUtils {

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
}
