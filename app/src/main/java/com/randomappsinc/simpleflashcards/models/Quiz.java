package com.randomappsinc.simpleflashcards.models;

import com.randomappsinc.simpleflashcards.constants.QuizScore;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.RandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Quiz {

    private static final int NUM_ANSWER_OPTIONS = 4;
    private static final float GOOD_PERCENTAGE_THRESHOLD = 80;
    private static final float OKAY_PERCENTAGE_THRESHOLD = 60;

    private ArrayList<Problem> problems;
    private int currentProblem = 0;

    public class Grade {
        private @QuizScore int score = QuizScore.BAD;
        private String fractionText;
        private String percentText;

        public @QuizScore int getScore() {
            return score;
        }

        void setScore(@QuizScore int score) {
            this.score = score;
        }

        public String getFractionText() {
            return fractionText;
        }

        void setFractionText(String fractionText) {
            this.fractionText = fractionText;
        }

        public String getPercentText() {
            return percentText;
        }

        void setPercentText(String percentText) {
            this.percentText = percentText;
        }
    }

    public Quiz(FlashcardSet flashcardSet, int numQuestions) {
        problems = new ArrayList<>();
        List<Flashcard> flashcards = flashcardSet.getFlashcards();

        // Indexes of the flashcards we are generating questions for
        List<Integer> indexes = RandUtils.getProblemIndexes(flashcards.size(), numQuestions);

        for (int index : indexes) {
            Flashcard flashcard = flashcards.get(index);

            Problem problem = new Problem();
            problem.setQuestion(flashcard.getTerm());
            problem.setQuestionImageUrl(flashcard.getTermImageUrl());
            problem.setAnswer(flashcard.getDefinition());

            int numOptions = Math.min(NUM_ANSWER_OPTIONS, flashcards.size());
            List<Integer> optionIndexes = RandUtils.getQuizChoicesIndexes(flashcards.size(), numOptions, index);
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

    public int getCurrentProblemPosition() {
        return currentProblem;
    }

    public int getNumQuestions() {
        return problems.size();
    }

    public Grade getGrade() {
        Grade grade = new Grade();
        int numCorrect = 0;
        for (Problem problem : problems) {
            if (problem.getAnswer().equals(problem.getGivenAnswer())) {
                numCorrect++;
            }
        }
        int totalQuestions = problems.size();
        grade.setFractionText(String.format(Locale.getDefault(), "%d/%d", numCorrect, totalQuestions));
        float percentage = ((float) numCorrect / (float) totalQuestions) * 100.0f;
        grade.setPercentText(String.format(Locale.getDefault(), "%.2f", percentage) + "%");
        if (percentage >= GOOD_PERCENTAGE_THRESHOLD) {
            grade.setScore(QuizScore.GOOD);
        } else if (percentage >= OKAY_PERCENTAGE_THRESHOLD) {
            grade.setScore(QuizScore.OKAY);
        }
        return grade;
    }

    public ArrayList<Problem> getProblems() {
        return problems;
    }
}
