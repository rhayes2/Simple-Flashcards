package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;

import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

public class ViewQuizletSetActivity extends StandardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_quizlet_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }
}
