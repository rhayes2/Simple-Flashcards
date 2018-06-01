package com.randomappsinc.simpleflashcards.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuizletFlashcardSet {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("terms")
    @Expose
    private List<QuizletFlashcard> flashcards;

    public List<QuizletFlashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<QuizletFlashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
