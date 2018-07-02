package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizSettingsActivity extends StandardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_settings);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());
    }

    @OnClick(R.id.start_quiz)
    public void startQuiz() {
        int flashcardSetId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        finish();
        startActivity(new Intent(
                this, QuizActivity.class)
                .putExtra(Constants.FLASHCARD_SET_ID_KEY, flashcardSetId));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
