package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;

import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

public class QuizletSearchActivity extends StandardActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizlet_search);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
