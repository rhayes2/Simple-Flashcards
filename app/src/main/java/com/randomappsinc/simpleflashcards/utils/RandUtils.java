package com.randomappsinc.simpleflashcards.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandUtils {

    // Returns the indexes of the flashcards we want to generate problems for
    public static List<Integer> getProblemIndexes(int numFlashcards, int numQuestions) {
        List<Integer> numbers = new ArrayList<>();

        // Mechanism to prevent duplicate problems
        Set<Integer> excludedNumsSet = new HashSet<>();

        int numAdded = 0;
        Random random = new Random();
        while (numAdded < numQuestions) {
            int attempt = random.nextInt(numFlashcards);
            if (!excludedNumsSet.contains(attempt)) {
                numbers.add(attempt);
                excludedNumsSet.add(attempt);
                numAdded++;
            }
        }

        Collections.shuffle(numbers);
        return numbers;
    }

    public static List<Integer> getQuizChoicesIndexes(int numQuestions, int quantity, int excludedIndex) {
        List<Integer> numbers = new ArrayList<>();

        // Mechanism to prevent duplicate options
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
