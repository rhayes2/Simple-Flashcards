package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
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
        quizResults.setAdapter(new QuizResultsAdapter(
                this,
                problems,
                flashcardSetSize,
                resultClickListener));
    }

    private final QuizResultsAdapter.Listener resultClickListener = new QuizResultsAdapter.Listener() {
        @Override
        public void onImageClicked(String imageUrl) {
            Intent intent = new Intent(QuizResultsActivity.this, PictureFullViewActivity.class)
                    .putExtra(Constants.IMAGE_URL_KEY, imageUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, 0);
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
