package com.randomappsinc.simpleflashcards.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.nhaarman.listviewanimations.util.Swappable;
import com.randomappsinc.simpleflashcards.Activities.EditFlashcardSetActivity;
import com.randomappsinc.simpleflashcards.Activities.MainActivity;
import com.randomappsinc.simpleflashcards.Persistence.DataObjects.FlashcardSet;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/20/15.
 */
public class FlashcardSetsAdapter extends BaseAdapter implements Swappable {
    private Context context;
    private List<String> setNames;
    private TextView noSets;

    public FlashcardSetsAdapter(Context context, TextView noSets) {
        this.context = context;
        this.noSets = noSets;
        refreshContent();
    }

    public void refreshContent() {
        List<FlashcardSet> sets = DatabaseManager.get().getAllFlashcardSets();
        this.setNames = new ArrayList<>(sets.size());
        for (int i = 0; i < sets.size(); i++) {
            setNames.add("");
        }
        for (FlashcardSet set : sets) {
            this.setNames.set(set.getPosition(), set.getName());
        }
        setNoContent();
        notifyDataSetChanged();
    }

    public void setNoContent() {
        int viewVisibility = setNames.isEmpty() ? View.VISIBLE : View.GONE;
        noSets.setVisibility(viewVisibility);
    }

    public int getCount()
    {
        return setNames.size();
    }

    public String getItem(int position)
    {
        return setNames.get(position);
    }

    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void swapItems(int positionOne, int positionTwo) {
        String setOne = setNames.get(positionOne);
        setNames.set(positionOne, setNames.get(positionTwo));
        setNames.set(positionTwo, setOne);
        notifyDataSetChanged();
    }

    public class FlashcardSetViewHolder {
        @Bind(R.id.set_name) public TextView setName;
        @Bind(R.id.edit_icon) public IconTextView edit;

        public FlashcardSetViewHolder(View view) {
            ButterKnife.bind(this, view);
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
        }
        else {
            holder = (FlashcardSetViewHolder) view.getTag();
        }

        holder.setName.setText(setNames.get(position));
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(context, EditFlashcardSetActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(MainActivity.FLASHCARD_SET_KEY, getItem(position));
                context.startActivity(intent);
            }
        });
        return view;
    }
}
