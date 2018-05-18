package com.randomappsinc.simpleflashcards.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandUtils {

    public static List<Integer> getQuizChoicesIndexes(int numQuestions, int quantity, int excludedIndex) {
        List<Integer> numbers = new ArrayList<>();
        Set<Integer> excludedNumsSet = new HashSet<>();
        excludedNumsSet.add(excludedIndex);
        int numAdded = 0;
        Random random = new Random();
        while (numAdded < quantity - 1) {
            int attempt = random.nextInt(numQuestions);
            if (!excludedNumsSet.contains(attempt)) {
                numbers.add(attempt);
                excludedNumsSet.add(attempt);
                numAdded++;
            }
        }
        // Add right answer back into mix
        numbers.add(excludedIndex);
        Collections.shuffle(numbers);
        return numbers;
    }
}
