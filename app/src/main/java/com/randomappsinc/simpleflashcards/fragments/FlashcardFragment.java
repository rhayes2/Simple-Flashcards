package com.randomappsinc.simpleflashcards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.Constants;

import butterknife.ButterKnife;
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

    private Flashcard flashcard;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.flashcard, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        int flashcardId = getArguments().getInt(Constants.FLASHCARD_ID_KEY);
        flashcard = DatabaseManager.get().getFlashcard(flashcardId);

        return rootView;
    }

    private void loadFlashcardIntoView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
