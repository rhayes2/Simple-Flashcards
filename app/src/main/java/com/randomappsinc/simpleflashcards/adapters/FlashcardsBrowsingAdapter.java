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

    private List<Integer> flashcardIds;

    public FlashcardsBrowsingAdapter(FragmentManager fragmentManager, int setId) {
        super(fragmentManager);
        List<Flashcard> flashcards = DatabaseManager.get().getAllFlashcards(setId);
        flashcardIds = new ArrayList<>();
        for (Flashcard flashcard : flashcards) {
            flashcardIds.add(flashcard.getId());
        }
    }

    public void shuffle() {
        Collections.shuffle(flashcardIds);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return FlashcardFragment.create(
                flashcardIds.get(position),
                position + 1,
                flashcardIds.size());
    }

    @Override
    public int getCount() {
        return flashcardIds.size();
    }
}
