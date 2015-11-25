package com.randomappsinc.simpleflashcards.Activities;

import android.os.Bundle;

import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class FlashcardFormActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }
}
