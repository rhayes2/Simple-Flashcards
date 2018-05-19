package com.randomappsinc.simpleflashcards.models;

import com.randomappsinc.simpleflashcards.constants.QuizScore;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.RandUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Quiz {

    private static final int NUM_ANSWER_OPTIONS = 4;
    private static final float GOOD_PERCENTAGE_THRESHOLD = 80;
    private static final float OKAY_PERCENTAGE_THRESHOLD = 60;

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

        void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<String> getOptions() {
            return options;
        }

        void setOptions(List<String> options) {
            this.options = options;
        }

        void setGivenAnswer(String givenAnswer) {
            this.givenAnswer = givenAnswer;
        }
    }

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

        public void setFractionText(String fractionText) {
            this.fractionText = fractionText;
        }

        public String getPercentText() {
            return percentText;
        }

        public void setPercentText(String percentText) {
            this.percentText = percentText;
        }
    }

    public Quiz(FlashcardSet flashcardSet) {
        problems = new ArrayList<>();
        List<Flashcard> flashcards = flashcardSet.getFlashcards();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < flashcards.size(); i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        for (int index : indexes) {
            Problem problem = new Problem();
            problem.setQuestion(flashcards.get(index).getTerm());
            problem.setAnswer(flashcards.get(index).getDefinition());

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
            if (problem.answer.equals(problem.givenAnswer)) {
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

    public List<Problem> getProblems() {
        return problems;
    }
}
