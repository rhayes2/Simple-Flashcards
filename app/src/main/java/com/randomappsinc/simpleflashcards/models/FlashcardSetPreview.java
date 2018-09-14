package com.randomappsinc.simpleflashcards.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

/**
 * Used to show added flashcard sets from data restoration.
 */
public class FlashcardSetPreview implements Parcelable {

    private String setName;
    private int numCards;

    public FlashcardSetPreview(FlashcardSet flashcardSet) {
        this.setName = flashcardSet.getName();
        this.numCards = flashcardSet.getFlashcards().size();
    }

    public String getSetName() {
        return setName;
    }

    public int getNumCards() {
        return numCards;
    }

    protected FlashcardSetPreview(Parcel in) {
        setName = in.readString();
        numCards = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(setName);
        dest.writeInt(numCards);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FlashcardSetPreview> CREATOR
            = new Parcelable.Creator<FlashcardSetPreview>() {
        @Override
        public FlashcardSetPreview createFromParcel(Parcel in) {
            return new FlashcardSetPreview(in);
        }

        @Override
        public FlashcardSetPreview[] newArray(int size) {
            return new FlashcardSetPreview[size];
        }
    };
}
