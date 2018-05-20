package com.randomappsinc.simpleflashcards.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Problem implements Parcelable {

    private String question;
    private String answer;
    private List<String> options;
    private String givenAnswer;

    Problem() {}

    public String getQuestion() {
        return question;
    }

    void setQuestion(String question) {
        this.question = question;
    }

    String getAnswer() {
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

    protected Problem(Parcel in) {
        question = in.readString();
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
        dest.writeString(question);
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
