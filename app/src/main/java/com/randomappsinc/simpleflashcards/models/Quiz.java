package com.randomappsinc.simpleflashcards.models;

import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.RandUtils;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private static final int NUM_ANSWER_OPTIONS = 4;

    private List<Problem> problems;
    private int currentProblem = 0;

    public class Problem {
        private String question;
        private String answer;
        private List<String> options;
        private String givenAnswer;

        public String getQuestion() {
            return question;
        }

        void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<String> getOptions() {
            return options;
        }

        void setOptions(List<String> options) {
            this.options = options;
        }

        public String getGivenAnswer() {
            return givenAnswer;
        }

        void setGivenAnswer(String givenAnswer) {
            this.givenAnswer = givenAnswer;
        }
    }

    public Quiz(FlashcardSet flashcardSet) {
        problems = new ArrayList<>();
        List<Flashcard> flashcards = flashcardSet.getFlashcards();
        for (int i = 0; i < flashcards.size(); i++) {
            Problem problem = new Problem();
            problem.setQuestion(flashcards.get(i).getTerm());
            problem.setAnswer(flashcards.get(i).getDefinition());

            int numOptions = Math.min(NUM_ANSWER_OPTIONS, flashcards.size());
            List<Integer> optionIndexes = RandUtils.getQuizChoicesIndexes(flashcards.size(), numOptions, i);
            List<String> options = new ArrayList<>(optionIndexes.size());
            for (int j : optionIndexes) {
                options.add(flashcards.get(j).getDefinition());
            }
            problem.setOptions(options);
            problems.add(problem);
        }
    }

    public void advanceToNextProblem() {
        currentProblem++;
    }

    public void submitAnswer(String answer) {
        problems.get(currentProblem).setGivenAnswer(answer);
    }

    public Problem getCurrentProblem() {
        return problems.get(currentProblem);
    }

    public int getNumOptions() {
        return problems.get(0).getOptions().size();
    }

    public boolean isQuizComplete() {
        return currentProblem >= problems.size();
    }
}
