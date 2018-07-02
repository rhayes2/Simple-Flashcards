package com.randomappsinc.simpleflashcards.models;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizSettings implements Parcelable {

    private int numQuestions;
    private int numMinutes;

    public QuizSettings(int numQuestions, int numMinutes) {
        this.numQuestions = numQuestions;
        this.numMinutes = numMinutes;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public int getNumMinutes() {
        return numMinutes;
    }

    protected QuizSettings(Parcel in) {
        numQuestions = in.readInt();
        numMinutes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numQuestions);
        dest.writeInt(numMinutes);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QuizSettings> CREATOR = new Parcelable.Creator<QuizSettings>() {
        @Override
        public QuizSettings createFromParcel(Parcel in) {
            return new QuizSettings(in);
        }

        @Override
        public QuizSettings[] newArray(int size) {
            return new QuizSettings[size];
        }
    };
}
