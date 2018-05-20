package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.Problem;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.QuizResultViewHolder> {

    private Context context;
    private List<Problem> problems;
    private int flashcardSetSize;

    public QuizResultsAdapter(Context context, List<Problem> problems, int flashcardSetSize) {
        this.context = context;
        this.problems = problems;
        this.flashcardSetSize = flashcardSetSize;
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
        holder.loadResult(position);
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    class QuizResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.question_header) TextView questionHeader;
        @BindView(R.id.question) TextView question;
        @BindView(R.id.your_answer_icon) TextView yourAnswerIcon;
        @BindView(R.id.your_answer) TextView yourAnswer;
        @BindView(R.id.correct_answer_header) View correctAnswerHeader;
        @BindView(R.id.correct_answer_container) View correctAnswerContainer;
        @BindView(R.id.correct_answer) TextView correctAnswer;

        @BindColor(R.color.green) int green;
        @BindColor(R.color.red) int red;
        @BindString(R.string.quiz_question_header) String headerTemplate;

        QuizResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadResult(int position) {
            Problem problem = problems.get(position);
            String headerText = String.format(headerTemplate, position + 1, flashcardSetSize);
            questionHeader.setText(headerText);
            question.setText(problem.getQuestion());

            boolean wasUserCorrect = problem.wasUserCorrect();
            yourAnswerIcon.setText(wasUserCorrect ? R.string.check_icon : R.string.x_icon);
            yourAnswerIcon.setTextColor(wasUserCorrect ? green : red);
            yourAnswer.setText(problem.getGivenAnswer());

            if (wasUserCorrect) {
                correctAnswerHeader.setVisibility(View.GONE);
                correctAnswerContainer.setVisibility(View.GONE);
            } else {
                correctAnswer.setText(problem.getAnswer());
                correctAnswerHeader.setVisibility(View.VISIBLE);
                correctAnswerContainer.setVisibility(View.VISIBLE);
            }
        }
    }
}
