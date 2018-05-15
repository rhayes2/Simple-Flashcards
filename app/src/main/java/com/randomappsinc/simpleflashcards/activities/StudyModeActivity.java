package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.SeekBar;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsBrowsingAdapter;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.Constants;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

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
        FlashcardSet flashcardSet = DatabaseManager.get().getFlashcardSet(setId);
        setTitle(flashcardSet.getName());

        flashcardsBrowsingAdapter = new FlashcardsBrowsingAdapter(getSupportFragmentManager(), setId);
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);

        flashcardsSlider.setMax(flashcardSet.getFlashcards().size() - 1);
        flashcardsSlider.setOnSeekBarChangeListener(flashcardsSliderListener);
    }

    private final SeekBar.OnSeekBarChangeListener flashcardsSliderListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        flashcardsPager.setCurrentItem(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };

    @OnPageChange(R.id.flashcards_pager)
    public void onFlashcardChanged(int position) {
        flashcardsSlider.setProgress(position);
    }

    @OnClick(R.id.shuffle)
    public void shuffleFlashcards() {
        flashcardsBrowsingAdapter.shuffle();
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);
        flashcardsPager.setCurrentItem(0);
        flashcardsSlider.setProgress(0);
        UIUtils.showShortToast(R.string.flashcard_set_shuffled);
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }
}
