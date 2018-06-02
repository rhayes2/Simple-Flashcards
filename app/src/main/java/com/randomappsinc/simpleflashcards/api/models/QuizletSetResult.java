package com.randomappsinc.simpleflashcards.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuizletSetResult {

    @SerializedName("id")
    @Expose
    private long quizletSetId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("term_count")
    @Expose
    private int flashcardCount;

    @SerializedName("created_date")
    @Expose
    private long createdDate;

    @SerializedName("modified_date")
    @Expose
    private long modifiedDate;

    @SerializedName("has_images")
    @Expose
    private boolean hasImages;

    public long getQuizletSetId() {
        return quizletSetId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFlashcardCount() {
        return flashcardCount;
    }

    public long getCreatedDate() {
        return createdDate * 1000L;
    }

    public long getModifiedDate() {
        return modifiedDate * 1000L;
    }

    public boolean hasImages() {
        return hasImages;
    }
}
