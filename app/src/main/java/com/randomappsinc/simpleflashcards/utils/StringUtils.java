package com.randomappsinc.simpleflashcards.utils;

import java.util.HashMap;

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

    public static HashMap<String, Integer> getWordAmounts(String[] splits) {
        HashMap<String, Integer> wordAmounts = new HashMap<>();
        for (String answerWord : splits) {
            String cleanWord = answerWord.toLowerCase();
            if (wordAmounts.containsKey(cleanWord)) {
                int currentAmount = wordAmounts.get(cleanWord);
                wordAmounts.put(cleanWord, currentAmount + 1);
            } else {
                wordAmounts.put(cleanWord, 1);
            }
        }
        return wordAmounts;
    }
}
