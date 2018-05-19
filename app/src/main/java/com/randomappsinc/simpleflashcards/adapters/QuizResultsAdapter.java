package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.Problem;

import java.util.List;

import butterknife.ButterKnife;

public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.QuizResultViewHolder> {

    private Context context;
    private List<Problem> problems;

    public QuizResultsAdapter(Context context, List<Problem> problems) {
        this.context = context;
        this.problems = problems;
    }

    @NonNull
    @Override
    public QuizResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.quiz_result_item_cell,
                parent,
                false);
        return new QuizResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizResultViewHolder holder, int position) {
        holder.loadSetting(position);
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    class QuizResultViewHolder extends RecyclerView.ViewHolder {

        QuizResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadSetting(int position) {
        }
    }
}
