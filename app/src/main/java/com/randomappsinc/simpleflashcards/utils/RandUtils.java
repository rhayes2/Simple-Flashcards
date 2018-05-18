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
        Set<Integer> excludedNumsSet = new HashSet<>(excludedIndex);
        int numAdded = 0;
        Random random = new Random();
        while (numAdded < quantity) {
            int attempt = random.nextInt(numQuestions);
            if (!excludedNumsSet.contains(attempt)) {
                numbers.add(attempt);
                excludedNumsSet.add(attempt);
                numAdded++;
            }
        }
        numbers.add(excludedIndex);
        Collections.shuffle(numbers);
        return numbers;
    }
}
