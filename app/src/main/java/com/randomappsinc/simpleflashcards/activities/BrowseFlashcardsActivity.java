package com.randomappsinc.simpleflashcards.activities;

import android.hardware.SensorManager;
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
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.DialogUtil;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
import com.squareup.seismic.ShakeDetector;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class BrowseFlashcardsActivity extends StandardActivity implements ShakeDetector.Listener {

    private static final float DISABLED_ALPHA = 0.25f;

    @BindView(R.id.flashcards_pager) ViewPager flashcardsPager;
    @BindView(R.id.flashcards_slider) SeekBar flashcardsSlider;
    @BindView(R.id.shake_toggle) View shakeToggle;
    @BindView(R.id.term_definition_toggle) TextView defaultSideToggle;
    @BindView(R.id.shuffle) View shuffleToggle;

    private FlashcardsBrowsingAdapter flashcardsBrowsingAdapter;
    private TextToSpeechManager textToSpeechManager;
    private BrowseFlashcardsSettingsManager settingsManager = BrowseFlashcardsSettingsManager.get();
    private PreferencesManager preferencesManager;
    private Random random;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_flashcards);
        ButterKnife.bind(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        textToSpeechManager = new TextToSpeechManager(this, textToSpeechListener);

        preferencesManager = new PreferencesManager(this);

        int setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        FlashcardSet flashcardSet = DatabaseManager.get().getFlashcardSet(setId);
        setTitle(flashcardSet.getName());

        flashcardsBrowsingAdapter = new FlashcardsBrowsingAdapter(getSupportFragmentManager(), setId);
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);

        flashcardsSlider.setMax(flashcardSet.getFlashcards().size() - 1);
        flashcardsSlider.setOnSeekBarChangeListener(flashcardsSliderListener);

        shakeToggle.setAlpha(preferencesManager.isShakeEnabled() ? 1.0f : DISABLED_ALPHA);

        settingsManager.addListener(defaultSideListener);

        random = new Random();
        shakeDetector = new ShakeDetector(this);
        if (preferencesManager.shouldShowShakeAdvice()) {
            DialogUtil.showDialogWithIconTextBody(
                    this,
                    R.string.shake_now_supported,
                    R.string.shake_it,
                    android.R.string.ok);
        }
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

    private final TextToSpeechManager.Listener textToSpeechListener = new TextToSpeechManager.Listener() {
        @Override
        public void onTextToSpeechFailure() {
            UIUtils.showLongToast(
                    R.string.text_to_speech_fail,
                    BrowseFlashcardsActivity.this);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    @Override
    public void hearShake() {
        int index = random.nextInt(flashcardsBrowsingAdapter.getCount());
        flashcardsPager.setCurrentItem(index);
    }

    @OnPageChange(R.id.flashcards_pager)
    public void onFlashcardChanged(int position) {
        textToSpeechManager.stopSpeaking();
        flashcardsSlider.setProgress(position);
    }

    @OnClick(R.id.shake_toggle)
    public void toggleShakeToChoose() {
        if (shakeToggle.getAlpha() < 1) {
            shakeToggle.setAlpha(1.0f);
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
            preferencesManager.setShakeEnabled(true);
            UIUtils.showShortToast(R.string.shake_to_jump_enabled, this);
        } else {
            shakeToggle.setAlpha(DISABLED_ALPHA);
            shakeDetector.stop();
            preferencesManager.setShakeEnabled(false);
            UIUtils.showShortToast(R.string.shake_to_jump_disabled, this);
        }
    }

    @OnClick(R.id.term_definition_toggle)
    public void toggleDefaultSide() {
        settingsManager.toggleDefaultSide();
        defaultSideToggle.setText(settingsManager.getShowTermsByDefault() ? R.string.t : R.string.d);
    }

    private final BrowseFlashcardsSettingsManager.Listener defaultSideListener =
            new BrowseFlashcardsSettingsManager.Listener() {
                @Override
                public void onDefaultSideChanged(boolean showTermsByDefault) {
                    UIUtils.showShortToast(showTermsByDefault
                            ? R.string.now_terms_default
                            : R.string.now_definitions_default,
                            BrowseFlashcardsActivity.this);
                }
            };

    @OnClick(R.id.shuffle)
    public void shuffleFlashcards() {
        flashcardsBrowsingAdapter.toggleShuffle();
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);
        flashcardsPager.setCurrentItem(0);
        flashcardsSlider.setProgress(0);
        shuffleToggle.setAlpha(shuffleToggle.getAlpha() < 1 ? 1.0f : DISABLED_ALPHA);
        UIUtils.showShortToast(
                flashcardsBrowsingAdapter.isShuffled()
                        ? R.string.flashcards_shuffled
                        : R.string.flashcards_order_restored,
                this);
    }

    public void speak(String text) {
        textToSpeechManager.speak(text);
    }

    public void stopSpeaking() {
        textToSpeechManager.stopSpeaking();
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        textToSpeechManager.stopSpeaking();
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
    }

    @Override
    public void onDestroy() {
        textToSpeechManager.shutdown();
        settingsManager.shutdown();
        super.onDestroy();
    }
}
