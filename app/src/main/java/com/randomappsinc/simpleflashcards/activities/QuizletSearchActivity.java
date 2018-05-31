package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizletSearchResultsAdapter;
import com.randomappsinc.simpleflashcards.api.QuizletSearchManager;
import com.randomappsinc.simpleflashcards.api.models.QuizletSetResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class QuizletSearchActivity extends StandardActivity {

    @BindView(R.id.flashcard_set_search) EditText setSearch;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.no_search_query_text) View noSearchQueryText;
    @BindView(R.id.quizlet_attribution) View quizletAttribution;
    @BindView(R.id.search_results) RecyclerView searchResults;

    protected QuizletSearchResultsAdapter adapter;
    private QuizletSearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizlet_search);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchManager = QuizletSearchManager.getInstance();
        searchManager.setListener(searchListener);

        adapter = new QuizletSearchResultsAdapter(this, resultClickListener);
        searchResults.setAdapter(adapter);
    }

    @OnTextChanged(value = R.id.flashcard_set_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        if (input.length() > 0) {
            searchManager.performSearch(input.toString());
        }
        searchResults.setVisibility(View.GONE);
        noSearchQueryText.setVisibility(input.length() == 0 ? View.VISIBLE : View.GONE);
        quizletAttribution.setVisibility(input.length() == 0 ? View.VISIBLE : View.GONE);
        clearSearch.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        setSearch.setText("");
    }

    private final QuizletSearchManager.Listener searchListener = new QuizletSearchManager.Listener() {
        @Override
        public void onResultsFetched(List<QuizletSetResult> results) {
            adapter.setResults(results);
            if (results.isEmpty()) {

            } else {
                searchResults.setVisibility(View.VISIBLE);
            }
        }
    };

    private final QuizletSearchResultsAdapter.Listener resultClickListener = new QuizletSearchResultsAdapter.Listener() {
        @Override
        public void onResultClicked(QuizletSetResult result) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchManager.clearEverything();
    }
}
