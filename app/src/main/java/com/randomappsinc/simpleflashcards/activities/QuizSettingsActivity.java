package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class QuizSettingsActivity extends StandardActivity {

    @BindView(R.id.num_questions) TextView numQuestions;
    @BindView(R.id.no_time_limit) CheckBox noTimeLimit;
    @BindView(R.id.set_time_limit) CheckBox setTimeLimit;

    int numFlashcards;

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

        int flashcardSetId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        numFlashcards = DatabaseManager.get().getFlashcardSet(flashcardSetId).getFlashcards().size();
        numQuestions.setText(String.valueOf(numFlashcards));
    }

    @OnClick(R.id.minus_5_questions)
    public void remove5Questions() {
        int current = Integer.valueOf(numQuestions.getText().toString());
        current = Math.max(1, current - 5);
        numQuestions.setText(String.valueOf(current));
    }

    @OnClick(R.id.minus_1_question)
    public void remove1Question() {
        int current = Integer.valueOf(numQuestions.getText().toString());
        current = Math.max(1, current - 1);
        numQuestions.setText(String.valueOf(current));
    }

    @OnClick(R.id.plus_one_question)
    public void add1Question() {
        int current = Integer.valueOf(numQuestions.getText().toString());
        current = Math.min(numFlashcards, current + 1);
        numQuestions.setText(String.valueOf(current));
    }

    @OnClick(R.id.plus_5_questions)
    public void add5Questions() {
        int current = Integer.valueOf(numQuestions.getText().toString());
        current = Math.min(numFlashcards, current + 5);
        numQuestions.setText(String.valueOf(current));
    }

    @OnClick(R.id.start_quiz)
    public void startQuiz() {
        int flashcardSetId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        finish();
        startActivity(new Intent(
                this, QuizActivity.class)
                .putExtra(Constants.FLASHCARD_SET_ID_KEY, flashcardSetId));
    }

    @OnCheckedChanged(R.id.no_time_limit)
    public void noTimeLimitSelected(boolean isChecked) {
        if (isChecked) {
            noTimeLimit.setClickable(false);
            setTimeLimit.setChecked(false);
            setTimeLimit.setClickable(true);
        }
    }

    @OnCheckedChanged(R.id.set_time_limit)
    public void setTimeLimitSelected(boolean isChecked) {
        if (isChecked) {
            setTimeLimit.setClickable(false);
            noTimeLimit.setChecked(false);
            noTimeLimit.setClickable(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
