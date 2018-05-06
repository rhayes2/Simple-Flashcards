package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlashcardsAdapter extends BaseAdapter {

    private Context context;
    private List<Flashcard> flashcards;
    private View noContent;
    private int setId;

    public FlashcardsAdapter(Context context, int setId, View noContent) {
        this.context = context;
        this.setId = setId;
        this.flashcards = DatabaseManager.get().getAllFlashcards(setId);
        this.noContent = noContent;
        setNoContent();
    }

    private void setNoContent() {
        int visibility = flashcards.size() == 0 ? View.VISIBLE : View.GONE;
        noContent.setVisibility(visibility);
    }

    public void refreshSet() {
        this.flashcards = DatabaseManager.get().getAllFlashcards(setId);
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
        @BindView(R.id.question) public TextView question;
        @BindView(R.id.answer) public TextView answer;

        FlashcardViewHolder(View view) {
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
