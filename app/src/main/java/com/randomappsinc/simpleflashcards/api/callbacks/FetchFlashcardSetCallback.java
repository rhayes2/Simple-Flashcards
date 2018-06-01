package com.randomappsinc.simpleflashcards.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.simpleflashcards.api.ApiConstants;
import com.randomappsinc.simpleflashcards.api.QuizletFlashcardSetFetcher;
import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcardSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchFlashcardSetCallback implements Callback<QuizletFlashcardSet> {

    @Override
    public void onResponse(@NonNull Call<QuizletFlashcardSet> call, @NonNull Response<QuizletFlashcardSet> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            QuizletFlashcardSet flashcardSet = response.body();
            if (flashcardSet != null) {
                QuizletFlashcardSetFetcher.getInstance().onFlashcardSetFetched(flashcardSet);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<QuizletFlashcardSet> call, @NonNull Throwable t) {

    }
}
