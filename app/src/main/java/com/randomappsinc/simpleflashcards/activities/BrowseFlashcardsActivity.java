package com.randomappsinc.simpleflashcards.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsBrowsingAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.BrowseFlashcardsSettingsManager;
import com.randomappsinc.simpleflashcards.managers.TextToSpeechManager;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class BrowseFlashcardsActivity extends StandardActivity {

    private static final float UNSHUFFLE_ALPHA = 0.25f;

    @BindView(R.id.flashcards_pager) ViewPager flashcardsPager;
    @BindView(R.id.flashcards_slider) SeekBar flashcardsSlider;
    @BindView(R.id.term_definition_toggle) TextView defaultSideToggle;
    @BindView(R.id.shuffle) View shuffleToggle;

    private FlashcardsBrowsingAdapter flashcardsBrowsingAdapter;
    private TextToSpeechManager textToSpeechManager = TextToSpeechManager.get();
    private BrowseFlashcardsSettingsManager settingsManager = BrowseFlashcardsSettingsManager.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_flashcards);
        ButterKnife.bind(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
        textToSpeechManager.stopSpeaking();
        flashcardsSlider.setProgress(position);
    }

    @OnClick(R.id.term_definition_toggle)
    public void toggleDefaultSide() {
        settingsManager.toggleDefaultSide();
        defaultSideToggle.setText(settingsManager.getShowTermsByDefault() ? R.string.t : R.string.d);
    }

    @OnClick(R.id.shuffle)
    public void shuffleFlashcards() {
        flashcardsBrowsingAdapter.toggleShuffle();
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);
        flashcardsPager.setCurrentItem(0);
        flashcardsSlider.setProgress(0);
        shuffleToggle.setAlpha(shuffleToggle.getAlpha() < 1 ? 1.0f : UNSHUFFLE_ALPHA);
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        textToSpeechManager.stopSpeaking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        settingsManager.shutdown();
    }
}
