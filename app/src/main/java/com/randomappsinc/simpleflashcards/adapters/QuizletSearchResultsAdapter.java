package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.api.models.QuizletSetResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class QuizletSearchResultsAdapter extends RecyclerView.Adapter<QuizletSearchResultsAdapter.ResultViewHolder> {

    private Context context;
    protected List<QuizletSetResult> results = new ArrayList<>();

    public QuizletSearchResultsAdapter(Context context) {
        this.context = context;
    }

    public void setResults(List<QuizletSetResult> results) {
        this.results.clear();
        this.results.addAll(results);
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.quiz_result_item_cell,
                parent,
                false);
        return new ResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.loadResult(position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

        ResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadResult(int position) {
        }
    }
}
