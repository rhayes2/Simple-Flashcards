package com.randomappsinc.simpleflashcards.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardUtils {

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
