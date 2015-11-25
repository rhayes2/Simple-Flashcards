package com.randomappsinc.simpleflashcards.Activities;

import android.os.Bundle;

import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class EditFlashcardSetActivity extends StandardActivity {
    public static final String FLASHCARD_SET_KEY = "flashcardSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flashcard_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        String flashcardSetName = getIntent().getStringExtra(FLASHCARD_SET_KEY);

        setTitle(getString(R.string.editing) + flashcardSetName);
    }
}
