package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlashcardSetsAdapter extends BaseAdapter {

    public interface Listener {
        void browseFlashcardSet(FlashcardSet flashcardSet);

        void takeQuiz(FlashcardSet flashcardSet);

        void editFlashcardSet(FlashcardSet flashcardSet);

        void deleteFlashcardSet(FlashcardSet flashcardSet);
    }

    @NonNull protected Listener listener;
    private Context context;
    protected List<FlashcardSet> flashcardSets;
    private TextView noSets;

    public FlashcardSetsAdapter(@NonNull Listener listener, Context context, TextView noSets) {
        this.listener = listener;
        this.context = context;
        this.flashcardSets = new ArrayList<>();
        this.noSets = noSets;
    }

    public void refreshContent(String searchTerm) {
        flashcardSets.clear();
        flashcardSets.addAll(DatabaseManager.get().getFlashcardSets(searchTerm));
        if (flashcardSets.isEmpty()) {
            noSets.setText(DatabaseManager.get().getNumFlashcardSets() == 0
                    ? R.string.no_sets_at_all
                    : R.string.no_sets_search);
            noSets.setVisibility(View.VISIBLE);
        } else {
            noSets.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return flashcardSets.size();
    }

    @Override
    public FlashcardSet getItem(int position) {
        return flashcardSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public class FlashcardSetViewHolder {
        @BindView(R.id.set_name) TextView setName;
        @BindView(R.id.num_flashcards) TextView numFlashcards;

        private int position;

        FlashcardSetViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void loadFlashcardSet(int position) {
            this.position = position;
            FlashcardSet flashcardSet = flashcardSets.get(position);
            setName.setText(flashcardSet.getName());
            numFlashcards.setText(String.valueOf(flashcardSet.getFlashcards().size()));
        }

        @OnClick(R.id.browse_button)
        public void browseFlashcards() {
            listener.browseFlashcardSet(getItem(position));
        }

        @OnClick(R.id.quiz_button)
        public void takeQuiz() {
            listener.takeQuiz(getItem(position));
        }

        @OnClick(R.id.edit_button)
        public void editFlashcardSet() {
            listener.editFlashcardSet(getItem(position));
        }

        @OnClick(R.id.delete_button)
        public void deleteFlashcardSet() {
            listener.deleteFlashcardSet(getItem(position));
        }
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(final int position, View view, ViewGroup parent) {
        FlashcardSetViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.flashcard_set_cell, parent, false);
            holder = new FlashcardSetViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (FlashcardSetViewHolder) view.getTag();
        }
        holder.loadFlashcardSet(position);
        return view;
    }
}
