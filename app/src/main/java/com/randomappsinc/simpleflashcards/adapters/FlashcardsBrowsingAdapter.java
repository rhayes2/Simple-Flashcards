package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.simpleflashcards.fragments.FlashcardFragment;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardsBrowsingAdapter extends FragmentStatePagerAdapter {

    private List<Integer> originalFlashcardIds = new ArrayList<>();
    private List<Integer> shuffledFlashcardsIds = new ArrayList<>();
    private boolean isShuffled = false;

    public FlashcardsBrowsingAdapter(FragmentManager fragmentManager, int setId) {
        super(fragmentManager);
        List<Flashcard> flashcards = DatabaseManager.get().getAllFlashcards(setId);
        for (Flashcard flashcard : flashcards) {
            originalFlashcardIds.add(flashcard.getId());
            shuffledFlashcardsIds.add(flashcard.getId());
        }
    }

    public void toggleShuffle() {
        isShuffled = !isShuffled;
        if (isShuffled) {
            Collections.shuffle(shuffledFlashcardsIds);
        }
    }

    public boolean isShuffled() {
        return isShuffled;
    }

    @Override
    public Fragment getItem(int position) {
        List<Integer> flashcardIds = isShuffled ? shuffledFlashcardsIds : originalFlashcardIds;
        return FlashcardFragment.create(
                flashcardIds.get(position),
                position + 1,
                flashcardIds.size());
    }

    @Override
    public int getCount() {
        return originalFlashcardIds.size();
    }
}
