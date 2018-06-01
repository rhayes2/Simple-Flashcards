package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizletFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.api.QuizletFlashcardSetFetcher;
import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcardSet;
import com.randomappsinc.simpleflashcards.constants.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewQuizletSetActivity extends StandardActivity {

    @BindView(R.id.flashcards) RecyclerView flashcards;

    protected QuizletFlashcardsAdapter adapter;
    private QuizletFlashcardSetFetcher setFetcher;
    protected QuizletFlashcardSet quizletSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_quizlet_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra(Constants.QUIZLET_SET_TITLE);
        setTitle(title);

        adapter = new QuizletFlashcardsAdapter(this);
        flashcards.setAdapter(adapter);

        setFetcher = QuizletFlashcardSetFetcher.getInstance();
        setFetcher.setListener(setFetchedListener);

        long setId = getIntent().getLongExtra(Constants.QUIZLET_SET_ID, 0L);
        setFetcher.fetchSet(setId);
    }

    private final QuizletFlashcardSetFetcher.Listener setFetchedListener = new QuizletFlashcardSetFetcher.Listener() {
        @Override
        public void onFlashcardSetFetched(QuizletFlashcardSet flashcardSet) {
            quizletSet = flashcardSet;
            adapter.loadFlashcards(flashcardSet.getFlashcards());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        setFetcher.clearEverything();
    }
}
