package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.simpleflashcards.fragments.FlashcardFragment;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;

import java.util.Collections;
import java.util.List;

public class FlashcardsBrowsingAdapter extends FragmentStatePagerAdapter {

    private List<Flashcard> flashcards;

    public FlashcardsBrowsingAdapter(FragmentManager fragmentManager, int setId) {
        super(fragmentManager);
        this.flashcards = DatabaseManager.get().getAllFlashcards(setId);
    }

    public void shuffle() {
        Collections.shuffle(flashcards);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return FlashcardFragment.create(flashcards.get(position).getId(), position, flashcards.size());
    }

    @Override
    public int getCount() {
        return flashcards.size();
    }
}
