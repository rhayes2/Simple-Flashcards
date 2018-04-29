package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.activities.EditFlashcardSetActivity;
import com.randomappsinc.simpleflashcards.activities.MainActivity;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlashcardSetsAdapter extends BaseAdapter {

    private Context context;
    private List<String> setNames;
    private TextView noSets;

    public FlashcardSetsAdapter(Context context, TextView noSets) {
        this.context = context;
        this.setNames = new ArrayList<>();
        this.noSets = noSets;
    }

    public void refreshContent(String searchTerm) {
        setNames.clear();
        setNames.addAll(DatabaseManager.get().getFlashcardSets(searchTerm));
        setNoContent();
        notifyDataSetChanged();
    }

    private void setNoContent() {
        int viewVisibility = setNames.isEmpty() ? View.VISIBLE : View.GONE;
        noSets.setVisibility(viewVisibility);
    }

    public int getCount() {
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

    public class FlashcardSetViewHolder {
        @BindView(R.id.set_name) public TextView setName;
        @BindView(R.id.edit_icon) public IconTextView edit;

        FlashcardSetViewHolder(View view) {
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
