package com.randomappsinc.simpleflashcards.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.constants.QuestionType;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.RandUtils;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Problem implements Parcelable {

    private static final int NUM_ANSWER_OPTIONS = 4;

    private @QuestionType int questionType;
    private int questionNumber;
    private String question;
    @Nullable private String questionImageUrl;
    private String answer;
    @Nullable private List<String> options;
    private String givenAnswer;

    Problem(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void setAsMultipleChoiceQuestion(Flashcard flashcard, int index, List<Flashcard> flashcards) {
        questionType = QuestionType.MULTIPLE_CHOICE;
        question = flashcard.getTerm();
        questionImageUrl = flashcard.getTermImageUrl();
        answer = flashcard.getDefinition();

        int numOptions = Math.min(NUM_ANSWER_OPTIONS, flashcards.size());
        List<Integer> optionIndexes = RandUtils.getQuizChoicesIndexes(flashcards.size(), numOptions, index);
        List<String> options = new ArrayList<>(optionIndexes.size());
        for (int optionIndex : optionIndexes) {
            options.add(flashcards.get(optionIndex).getDefinition());
        }
        this.options = options;
    }

    public void setAsFreeFormInputQuestion(Flashcard flashcard) {
        questionType = QuestionType.FREE_FORM_INPUT;
        question = flashcard.getTerm();
        questionImageUrl = flashcard.getTermImageUrl();
        answer = flashcard.getDefinition();
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    void setQuestion(String question) {
        this.question = question;
    }

    @Nullable
    public String getQuestionImageUrl() {
        return questionImageUrl;
    }

    public String getAnswer() {
        return answer;
    }

    @Nullable
    public List<String> getOptions() {
        return options;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public boolean wasUserCorrect() {
        switch (questionType) {
            case QuestionType.MULTIPLE_CHOICE:
                return answer.equals(givenAnswer);
            case QuestionType.FREE_FORM_INPUT:
                return isFreeFormInputCloseEnoughMatch();
            default:
                throw new IllegalStateException("Unsupported question type");
        }
    }

    private boolean isFreeFormInputCloseEnoughMatch() {
        String[] answerSplits = answer.split("\\s+");
        HashMap<String, Integer> answerWords = StringUtils.getWordAmounts(answerSplits);
        HashMap<String, Integer> responseWords = StringUtils.getWordAmounts(givenAnswer.split("\\s+"));
        int allowedMisses = answerSplits.length / 10;
        int numMisses = 0;
        for (String responseWord : responseWords.keySet()) {
            int responseAmount = responseWords.get(responseWord);
            if (answerWords.containsKey(responseWord)) {
                int answerAmount = answerWords.get(responseWord);
                int newAmount = answerAmount - responseAmount;
                answerWords.put(responseWord, newAmount);
            } else {
                numMisses += responseAmount;
            }
        }
        for (String answerWord : answerWords.keySet()) {
            numMisses += Math.abs(answerWords.get(answerWord));
        }
        return numMisses <= allowedMisses;
    }

    protected Problem(Parcel in) {
        questionNumber = in.readInt();
        question = in.readString();
        questionImageUrl = in.readString();
        answer = in.readString();
        if (in.readByte() == 0x01) {
            options = new ArrayList<>();
            in.readList(options, String.class.getClassLoader());
        } else {
            options = null;
        }
        givenAnswer = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionNumber);
        dest.writeString(question);
        dest.writeString(questionImageUrl);
        dest.writeString(answer);
        if (options == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(options);
        }
        dest.writeString(givenAnswer);
    }

    @SuppressWarnings("unused")
    public static final Creator<Problem> CREATOR = new Creator<Problem>() {
        @Override
        public Problem createFromParcel(Parcel in) {
            return new Problem(in);
        }

        @Override
        public Problem[] newArray(int size) {
            return new Problem[size];
        }
    };
}
