package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizResultsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.models.Problem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizResultsActivity extends StandardActivity {

    @BindView(R.id.quiz_results) RecyclerView quizResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_results);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                .colorRes(R.color.white)
                .actionBarSize());

        String setName = getIntent().getStringExtra(Constants.FLASHCARD_SET_NAME_KEY);
        setTitle(setName);

        List<Problem> problems = getIntent().getParcelableArrayListExtra(Constants.QUIZ_RESULTS_KEY);
        int flashcardSetSize = getIntent().getIntExtra(Constants.FLASHCARD_SET_SIZE_KEY, 0);
        quizResults.setAdapter(new QuizResultsAdapter(this, problems, flashcardSetSize));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
