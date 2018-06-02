package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizletFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.api.QuizletFlashcardSetFetcher;
import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcardSet;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewQuizletSetActivity extends StandardActivity {

    @BindView(R.id.flashcards) RecyclerView flashcards;
    @BindView(R.id.download) TextView button;

    @BindColor(R.color.app_blue) int blue;
    @BindColor(R.color.green) int green;

    protected QuizletFlashcardsAdapter adapter;
    private QuizletFlashcardSetFetcher setFetcher;
    @Nullable protected QuizletFlashcardSet quizletSet;
    private DatabaseManager databaseManager;

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

        databaseManager = DatabaseManager.get();
        if (databaseManager.alreadyHasQuizletSet(setId)) {
            button.setText(R.string.saved_to_library);
            button.setBackgroundColor(green);
        } else {
            button.setText(R.string.download);
            button.setBackgroundColor(blue);
        }
    }

    private final QuizletFlashcardSetFetcher.Listener setFetchedListener = new QuizletFlashcardSetFetcher.Listener() {
        @Override
        public void onFlashcardSetFetched(QuizletFlashcardSet flashcardSet) {
            quizletSet = flashcardSet;
            adapter.loadFlashcards(flashcardSet.getFlashcards());
        }
    };

    @OnClick(R.id.download)
    public void download() {
        if (quizletSet != null && !databaseManager.alreadyHasQuizletSet(quizletSet.getQuizletSetId())) {
            databaseManager.saveQuizletSet(quizletSet);
            button.setText(R.string.saved_to_library);
            button.setBackgroundColor(green);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setFetcher.clearEverything();
    }
}
