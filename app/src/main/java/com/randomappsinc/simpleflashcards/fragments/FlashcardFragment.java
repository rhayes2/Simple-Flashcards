package com.randomappsinc.simpleflashcards.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.Constants;

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
    @BindView(R.id.content) TextView content;

    @BindInt(R.integer.default_anim_length) int flipAnimLength;

    private Flashcard flashcard;
    private boolean isShowingTerm = true;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.flashcard, container, false);
        unbinder = ButterKnife.bind(this, rootView);

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

    @OnClick(R.id.flashcard_container)
    public void flipFlashcard() {
        flashcardContainer.setEnabled(false);
        flashcardContainer.clearAnimation();
        flashcardContainer
                .animate()
                .rotationY(180)
                .setDuration(flipAnimLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isShowingTerm = !isShowingTerm;
                        positionInfo.setVisibility(View.GONE);
                        sideHeader.setVisibility(View.GONE);
                        speak.setVisibility(View.GONE);
                        content.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flashcardContainer.setRotationY(0);
                        positionInfo.setVisibility(View.VISIBLE);
                        loadFlashcardIntoView();
                        sideHeader.setVisibility(View.VISIBLE);
                        speak.setVisibility(View.VISIBLE);
                        content.setVisibility(View.VISIBLE);
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

    private void loadFlashcardIntoView() {
        sideHeader.setText(isShowingTerm ? R.string.term_underlined : R.string.definition_underlined);
        content.setText(isShowingTerm ? flashcard.getTerm() : flashcard.getDefinition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
