package com.randomappsinc.simpleflashcards.models;

import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.RandUtils;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private static final int NUM_ANSWER_OPTIONS = 4;

    private List<Question> questions;

    public class Question {
        private String question;
        private String answer;
        private List<String> options;
        private String givenAnswer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getGivenAnswer() {
            return givenAnswer;
        }

        public void setGivenAnswer(String givenAnswer) {
            this.givenAnswer = givenAnswer;
        }
    }

    public Quiz(FlashcardSet flashcardSet) {
        questions = new ArrayList<>();
        List<Flashcard> flashcards = flashcardSet.getFlashcards();
        for (int i = 0; i < flashcards.size(); i++) {
            Question question = new Question();
            question.setQuestion(flashcards.get(i).getTerm());
            question.setAnswer(flashcards.get(i).getDefinition());

            int numOptions = Math.min(NUM_ANSWER_OPTIONS, flashcards.size());
            List<Integer> optionIndexes = RandUtils.getQuizChoicesIndexes(flashcards.size(), numOptions, i);
            List<String> options = new ArrayList<>(optionIndexes.size());
            for (int j : optionIndexes) {
                options.add(flashcards.get(j).getDefinition());
            }
            question.setOptions(options);
            questions.add(question);
        }
    }
}
