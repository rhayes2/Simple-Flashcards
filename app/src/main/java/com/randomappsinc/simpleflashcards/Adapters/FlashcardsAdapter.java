package com.randomappsinc.simpleflashcards.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.Persistence.DataObjects.Flashcard;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class FlashcardsAdapter extends BaseAdapter {
    private Context context;
    private List<Flashcard> flashcards;
    private View noContent;
    private String setName;

    public FlashcardsAdapter(Context context, String setName, View noContent) {
        this.context = context;
        this.setName = setName;
        this.flashcards = DatabaseManager.get().getAllFlashcards(setName);
        this.noContent = noContent;
        setNoContent();
    }

    public void setNoContent() {
        int visibility = flashcards.size() == 0 ? View.VISIBLE : View.GONE;
        noContent.setVisibility(visibility);
    }

    public void refreshSet() {
        this.flashcards = DatabaseManager.get().getAllFlashcards(setName);
        setNoContent();
        notifyDataSetChanged();
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

    public class FlashcardViewHolder {
        @Bind(R.id.question) public TextView question;
        @Bind(R.id.answer) public TextView answer;

        public FlashcardViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        FlashcardViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.flashcard_cell, parent, false);
            holder = new FlashcardViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (FlashcardViewHolder) view.getTag();
        }

        String question = "<b>" + context.getString(R.string.question_prefix) + "</b>" + flashcards.get(position).getQuestion();
        holder.question.setText(Html.fromHtml(question));
        String answer = "<b>" + context.getString(R.string.answer_prefix) + "</b>" + flashcards.get(position).getAnswer();
        holder.answer.setText(Html.fromHtml(answer));

        return view;
    }
}
