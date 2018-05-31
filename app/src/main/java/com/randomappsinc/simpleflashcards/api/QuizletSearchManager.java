package com.randomappsinc.simpleflashcards.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.randomappsinc.simpleflashcards.api.models.QuizletSetResult;

import java.util.List;

/** Utility class to do Quizlet searches, so UI pieces don't need to do any networking **/
public class QuizletSearchManager {

    public interface Listener {
        void onResultsFetched(List<QuizletSetResult> results);
    }

    private static QuizletSearchManager instance;

    public static QuizletSearchManager getInstance() {
        if (instance == null) {
            instance = new QuizletSearchManager();
        }
        return instance;
    }

    @Nullable private Listener listener;
    private QuizletRestClient restClient;

    private QuizletSearchManager() {
        restClient = QuizletRestClient.getInstance();
    }

    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void performSearch(String searchTerm) {
        restClient.findFlashcardSets(searchTerm);
    }

    public void onFlashcardSetsFound(List<QuizletSetResult> flashcardSets) {
        if (listener != null) {
            listener.onResultsFetched(flashcardSets);
        }
    }

    public void clearEverything() {
        restClient.cancelFlashcardsSearch();
        listener = null;
    }
}
