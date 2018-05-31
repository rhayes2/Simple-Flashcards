package com.randomappsinc.simpleflashcards.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuizletSearchResults {

    @SerializedName("sets")
    @Expose
    private List<QuizletSetResult> results;

    public List<QuizletSetResult> getResults() {
        return results;
    }
}
