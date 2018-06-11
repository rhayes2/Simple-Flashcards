package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.api.models.QuizletSetResult;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizletSearchResultsAdapter
        extends RecyclerView.Adapter<QuizletSearchResultsAdapter.ResultViewHolder> {

    public interface Listener {
        void onResultClicked(QuizletSetResult result);
    }

    protected Context context;
    protected Listener listener;
    protected List<QuizletSetResult> results = new ArrayList<>();
    protected DatabaseManager databaseManager = DatabaseManager.get();

    public QuizletSearchResultsAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setResults(List<QuizletSetResult> results) {
        this.results.clear();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.quizlet_search_result_cell,
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

        @BindView(R.id.title) TextView title;
        @BindView(R.id.num_flashcards) TextView numFlashcardsText;
        @BindView(R.id.created_on) TextView createdOn;
        @BindView(R.id.last_updated) TextView lastUpdated;
        @BindView(R.id.side_info) View sideInfo;
        @BindView(R.id.images_icon) View imageIcon;
        @BindView(R.id.images_text) View imageText;
        @BindView(R.id.in_library) View inLibraryText;

        ResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadResult(int position) {
            QuizletSetResult result = results.get(position);
            title.setText(result.getTitle());
            int numFlashcards = result.getFlashcardCount();
            if (numFlashcards == 1) {
                numFlashcardsText.setText(R.string.one_flashcard);
            } else {
                numFlashcardsText.setText(context.getString(R.string.x_flashcards, numFlashcards));
            }
            createdOn.setText(context.getString(
                    R.string.created_on_template,
                    TimeUtils.getFlashcardSetTime(result.getCreatedDate())));
            lastUpdated.setText(context.getString(
                    R.string.last_updated_template,
                    TimeUtils.getFlashcardSetTime(result.getModifiedDate())));
            imageIcon.setVisibility(result.hasImages() ? View.VISIBLE : View.GONE);
            imageText.setVisibility(result.hasImages() ? View.VISIBLE : View.GONE);

            boolean setAlreadyInLibrary = databaseManager.alreadyHasQuizletSet(result.getQuizletSetId());
            inLibraryText.setVisibility(setAlreadyInLibrary ? View.VISIBLE : View.GONE);

            sideInfo.setVisibility(result.hasImages() || setAlreadyInLibrary ? View.VISIBLE : View.GONE);
        }

        @OnClick(R.id.parent)
        public void onCellClicked() {
            listener.onResultClicked(results.get(getAdapterPosition()));
        }
    }
}
