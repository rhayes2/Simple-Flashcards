package com.randomappsinc.simpleflashcards.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.randomappsinc.simpleflashcards.Persistence.DataObjects.Flashcard;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;

import java.util.List;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class FlashcardSetAdapter extends BaseAdapter {
    private Context context;
    private List<Flashcard> flashcards;
    private View noContent;

    public FlashcardSetAdapter(Context context, String setName, View noContent) {
        this.context = context;
        this.flashcards = DatabaseManager.get().getAllFlashcards(setName);
        this.noContent = noContent;
    }

    @Override
    public int getCount() {
        return flashcards.size();
    }

    @Override
    public Flashcard getItem(int position) {
        return flashcards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
