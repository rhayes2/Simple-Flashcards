package com.randomappsinc.simpleflashcards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizletSearchResultsAdapter;
import com.randomappsinc.simpleflashcards.api.QuizletSearchManager;
import com.randomappsinc.simpleflashcards.api.models.QuizletSetResult;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class QuizletSearchActivity extends StandardActivity {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 150;

    @BindView(R.id.parent) View parent;
    @BindView(R.id.flashcard_set_search) EditText setSearch;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.search_empty_text) TextView searchEmptyText;
    @BindView(R.id.quizlet_attribution) View quizletAttribution;
    @BindView(R.id.skeleton_results) View skeletonResults;
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
        searchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    UIUtils.closeKeyboard(QuizletSearchActivity.this);
                    parent.requestFocus();
                }
            }
        });

        if (setSearch.requestFocus()) {
            setSearch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm == null) {
                        return;
                    }
                    imm.showSoftInput(setSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }, MILLIS_DELAY_FOR_KEYBOARD);
        }
    }

    @OnTextChanged(value = R.id.flashcard_set_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        if (input.length() > 0) {

            searchManager.performSearch(input.toString());
        }
        searchResults.setVisibility(View.GONE);
        searchResults.scrollToPosition(0);
        skeletonResults.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);

        if (input.length() == 0) {
            searchEmptyText.setText(R.string.quizlet_search_empty_state);
        }
        searchEmptyText.setVisibility(input.length() == 0 ? View.VISIBLE : View.GONE);
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
            skeletonResults.setVisibility(View.GONE);
            adapter.setResults(results);
            if (results.isEmpty()) {
                searchEmptyText.setText(R.string.no_quizlet_results);
                searchEmptyText.setVisibility(View.VISIBLE);
            } else {
                searchResults.setVisibility(View.VISIBLE);
            }
        }
    };

    private final QuizletSearchResultsAdapter.Listener resultClickListener =
            new QuizletSearchResultsAdapter.Listener() {
                @Override
                public void onResultClicked(QuizletSetResult result) {
                    Intent intent = new Intent(
                            QuizletSearchActivity.this,
                            ViewQuizletSetActivity.class)
                            .putExtra(Constants.QUIZLET_SET_ID, result.getQuizletSetId())
                            .putExtra(Constants.QUIZLET_SET_TITLE, result.getTitle());
                    startActivity(intent);
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchManager.clearEverything();
    }
}
