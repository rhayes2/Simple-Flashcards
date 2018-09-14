package com.randomappsinc.simpleflashcards.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.FlashcardSetPreview;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlainSetViewAdapter extends RecyclerView.Adapter<PlainSetViewAdapter.FlashcardViewHolder> {

    public interface Listener {
        void onImageClicked(Flashcard flashcard);
    }

    protected Listener listener;
    protected List<Flashcard> flashcards;

    public PlainSetViewAdapter(Listener listener, FlashcardSetPreview setPreview) {
        this.listener = listener;
        this.flashcards = DatabaseManager.get().getAllFlashcards(setPreview.getSetId());
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.quizlet_flashcard_cell,
                parent,
                false);
        return new FlashcardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        holder.loadFlashcard(position);
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    class FlashcardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.position_info) TextView positionInfo;
        @BindView(R.id.term_text) TextView termText;
        @BindView(R.id.term_image) ImageView termImage;
        @BindView(R.id.definition) TextView definition;
        @BindString(R.string.flashcard_x_of_y) String flashcardXOfY;

        FlashcardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadFlashcard(int position) {
            Flashcard flashcard = flashcards.get(position);
            positionInfo.setText(String.format(flashcardXOfY, position + 1, getItemCount()));
            termText.setText(flashcard.getTerm());
            String imageUrl = flashcard.getTermImageUrl();
            if (imageUrl == null) {
                termImage.setVisibility(View.GONE);
            } else {
                Picasso.get().load(imageUrl).into(termImage);
                termImage.setVisibility(View.VISIBLE);
            }
            definition.setText(flashcard.getDefinition());
        }

        @OnClick(R.id.term_image)
        public void openImageInFullView() {
            Flashcard flashcard = flashcards.get(getAdapterPosition());
            if (!TextUtils.isEmpty(flashcard.getTermImageUrl())) {
                listener.onImageClicked(flashcard);
            }
        }
    }
}
