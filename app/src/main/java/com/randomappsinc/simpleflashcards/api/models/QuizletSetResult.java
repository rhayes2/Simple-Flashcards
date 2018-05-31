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
    private int createdDate;

    @SerializedName("modified_date")
    @Expose
    private int modifiedDate;

    public long getQuizletSetId() {
        return quizletSetId;
    }

    public void setQuizletSetId(long quizletSetId) {
        this.quizletSetId = quizletSetId;
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

    public void setFlashcardCount(int flashcardCount) {
        this.flashcardCount = flashcardCount;
    }

    public int getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(int createdDate) {
        this.createdDate = createdDate;
    }

    public int getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(int modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
