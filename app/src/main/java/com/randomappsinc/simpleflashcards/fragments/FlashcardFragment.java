package com.randomappsinc.simpleflashcards.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.activities.BrowseFlashcardsActivity;
import com.randomappsinc.simpleflashcards.activities.PictureFullViewActivity;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.BrowseFlashcardsSettingsManager;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlashcardFragment extends Fragment {

    public static FlashcardFragment create(int flashcardId, int flashcardPosition, int setSize) {
        FlashcardFragment flashcardFragment = new FlashcardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FLASHCARD_ID_KEY, flashcardId);
        bundle.putInt(Constants.FLASHCARD_POSITION_KEY, flashcardPosition);
        bundle.putInt(Constants.FLASHCARD_SET_SIZE_KEY, setSize);
        flashcardFragment.setArguments(bundle);
        return flashcardFragment;
    }

    @BindView(R.id.flashcard_container) View flashcardContainer;
    @BindView(R.id.position_info) TextView positionInfo;
    @BindView(R.id.side_header) TextView sideHeader;
    @BindView(R.id.speak) View speak;
    @BindView(R.id.term_image) ImageView termImage;
    @BindView(R.id.content) TextView content;
    @BindView(R.id.flip_icon) View flipIcon;

    @BindInt(R.integer.default_anim_length) int flipAnimLength;

    private Flashcard flashcard;
    protected boolean isShowingTerm;
    private Unbinder unbinder;
    private BrowseFlashcardsSettingsManager settingsManager = BrowseFlashcardsSettingsManager.get();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.flashcard_for_browsing,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);

        settingsManager.addListener(defaultSideListener);
        isShowingTerm = settingsManager.getShowTermsByDefault();

        int flashcardId = getArguments().getInt(Constants.FLASHCARD_ID_KEY);
        flashcard = DatabaseManager.get().getFlashcard(flashcardId);

        int cardPosition = getArguments().getInt(Constants.FLASHCARD_POSITION_KEY);
        int setSize = getArguments().getInt(Constants.FLASHCARD_SET_SIZE_KEY);

        String positionTemplate = "%d/%d";
        String positionText = String.format(Locale.US, positionTemplate, cardPosition, setSize);
        positionInfo.setText(positionText);

        loadFlashcardIntoView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.flashcard_container)
    public void flipFlashcard() {
        stopSpeaking();
        flashcardContainer.setEnabled(false);
        flashcardContainer.clearAnimation();
        flashcardContainer
                .animate()
                .rotationY(180)
                .setDuration(flipAnimLength)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isShowingTerm = !isShowingTerm;
                        positionInfo.setVisibility(View.GONE);
                        sideHeader.setVisibility(View.GONE);
                        speak.setVisibility(View.GONE);
                        content.setVisibility(View.GONE);
                        termImage.setVisibility(View.GONE);
                        flipIcon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flashcardContainer.setRotationY(0);
                        positionInfo.setVisibility(View.VISIBLE);
                        loadFlashcardIntoView();
                        sideHeader.setVisibility(View.VISIBLE);
                        speak.setVisibility(View.VISIBLE);
                        content.setVisibility(View.VISIBLE);
                        flipIcon.setVisibility(View.VISIBLE);
                        flashcardContainer.setEnabled(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        flashcardContainer.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    protected void loadFlashcardIntoView() {
        sideHeader.setText(isShowingTerm ? R.string.term_underlined : R.string.definition_underlined);
        content.setText(isShowingTerm ? flashcard.getTerm() : flashcard.getDefinition());
        String termImageUrl = flashcard.getTermImageUrl();
        if (isShowingTerm && !TextUtils.isEmpty(termImageUrl)) {
            Picasso.get().load(termImageUrl).into(termImage);
            termImage.setVisibility(View.VISIBLE);
        } else {
            termImage.setVisibility(View.GONE);
        }
    }

    private final BrowseFlashcardsSettingsManager.Listener defaultSideListener =
            new BrowseFlashcardsSettingsManager.Listener() {
                @Override
                public void onDefaultSideChanged(boolean showTermsByDefault) {
                    isShowingTerm = showTermsByDefault;
                    loadFlashcardIntoView();
                }
            };

    @OnClick(R.id.speak)
    public void speakFlashcard() {
        speak(isShowingTerm ? flashcard.getTerm() : flashcard.getDefinition());
    }

    private void speak(String text) {
        BrowseFlashcardsActivity activity = (BrowseFlashcardsActivity) getActivity();
        if (activity != null) {
            activity.speak(text);
        }
    }

    private void stopSpeaking() {
        BrowseFlashcardsActivity activity = (BrowseFlashcardsActivity) getActivity();
        if (activity != null) {
            activity.stopSpeaking();
        }
    }

    @OnClick(R.id.term_image)
    public void openImageInFullView() {
        String imageUrl = flashcard.getTermImageUrl();
        Activity activity = getActivity();
        if (!TextUtils.isEmpty(imageUrl) && activity != null) {
            Intent intent = new Intent(activity, PictureFullViewActivity.class)
                    .putExtra(Constants.IMAGE_URL_KEY, imageUrl)
                    .putExtra(Constants.CAPTION_KEY, flashcard.getTerm());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingsManager.removeListener(defaultSideListener);
        unbinder.unbind();
    }
}
