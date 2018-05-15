package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.SeekBar;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsBrowsingAdapter;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyModeActivity extends StandardActivity {

    @BindView(R.id.flashcards_pager) ViewPager flashcardsPager;
    @BindView(R.id.flashcards_slider) SeekBar flashcardsSlider;

    private FlashcardsBrowsingAdapter flashcardsBrowsingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_mode);
        ButterKnife.bind(this);

        int setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        setTitle(DatabaseManager.get().getSetName(setId));

        flashcardsBrowsingAdapter = new FlashcardsBrowsingAdapter(getSupportFragmentManager(), setId);
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);

        flashcardsSlider.setMax(100);
    }

    @OnClick(R.id.shuffle)
    public void shuffleFlashcards() {
        flashcardsBrowsingAdapter.shuffle();
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }
}
