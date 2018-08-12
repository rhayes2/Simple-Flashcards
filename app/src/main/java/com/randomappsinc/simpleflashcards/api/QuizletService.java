package com.randomappsinc.simpleflashcards.api;

import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcardSet;
import com.randomappsinc.simpleflashcards.api.models.QuizletSearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuizletService {

    @GET("search/sets")
    Call<QuizletSearchResults> findFlashcardSets(@Query("q") String term, @Query("images_only") int imagesOnly);

    @GET("sets/{id}")
    Call<QuizletFlashcardSet> getFlashcardSetInfo(@Path("id") long setId);
}
