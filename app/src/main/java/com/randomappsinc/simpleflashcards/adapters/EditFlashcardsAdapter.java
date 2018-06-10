package com.randomappsinc.simpleflashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.dialogs.DeleteFlashcardDialog;
import com.randomappsinc.simpleflashcards.dialogs.EditFlashcardDefinitionDialog;
import com.randomappsinc.simpleflashcards.dialogs.EditFlashcardTermDialog;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFlashcardsAdapter extends RecyclerView.Adapter<EditFlashcardsAdapter.FlashcardViewHolder> {

    protected Context context;
    protected List<Flashcard> flashcards;
    private View noContent;
    private int setId;
    protected DeleteFlashcardDialog deleteFlashcardDialog;
    protected EditFlashcardTermDialog editFlashcardTermDialog;
    protected EditFlashcardDefinitionDialog editFlashcardDefinitionDialog;
    private TextView numFlashcards;
    protected int selectedItemPosition = -1;

    public EditFlashcardsAdapter(Context context, int setId, View noContent, TextView numFlashcards) {
        this.context = context;
        this.setId = setId;
        this.noContent = noContent;
        this.deleteFlashcardDialog = new DeleteFlashcardDialog(context, flashcardDeleteListener);
        this.editFlashcardTermDialog = new EditFlashcardTermDialog(context, flashcardTermEditListener);
        this.editFlashcardDefinitionDialog = new EditFlashcardDefinitionDialog(
                context,
                flashcardDefinitionEditListener);
        this.numFlashcards = numFlashcards;
        refreshSet();
    }

    private final DeleteFlashcardDialog.Listener flashcardDeleteListener =
            new DeleteFlashcardDialog.Listener() {
                @Override
                public void onFlashcardSetDeleted() {
                    refreshSet();
                }
            };

    private final EditFlashcardTermDialog.Listener flashcardTermEditListener =
            new EditFlashcardTermDialog.Listener() {
                @Override
                public void onFlashcardTermEdited(String newTerm) {
                    if (selectedItemPosition < 0) {
                        return;
                    }
                    flashcards.get(selectedItemPosition).setTerm(newTerm);
                    notifyItemChanged(selectedItemPosition);
                    selectedItemPosition = -1;
                }
            };

    private final EditFlashcardDefinitionDialog.Listener flashcardDefinitionEditListener =
            new EditFlashcardDefinitionDialog.Listener() {
                @Override
                public void onFlashcardDefinitionEdited(String newDefinition) {
                    if (selectedItemPosition < 0) {
                        return;
                    }
                    flashcards.get(selectedItemPosition).setDefinition(newDefinition);
                    notifyItemChanged(selectedItemPosition);
                    selectedItemPosition = -1;
                }
            };

    private void setNoContent() {
        int visibility = flashcards.size() == 0 ? View.VISIBLE : View.GONE;
        noContent.setVisibility(visibility);
    }

    public void refreshSet() {
        this.flashcards = DatabaseManager.get().getAllFlashcards(setId);
        setNoContent();
        notifyDataSetChanged();
        refreshCount();
    }

    protected void refreshCount() {
        int flashcardsCount = getItemCount();
        String numFlashcardsText = flashcardsCount == 1
                ? context.getString(R.string.one_flashcard)
                : context.getString(R.string.x_flashcards, flashcardsCount);
        numFlashcards.setText(numFlashcardsText);
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.flashcard_editing_cell,
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

    public class FlashcardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.position_info) TextView positionInfo;
        @BindView(R.id.term_text) TextView termText;
        @BindView(R.id.term_image) ImageView termImage;
        @BindView(R.id.definition) TextView definition;

        FlashcardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadFlashcard(int position) {
            Flashcard flashcard = flashcards.get(position);
            positionInfo.setText(context.getString(
                    R.string.flashcard_x_of_y,
                    position + 1,
                    getItemCount()));
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

        @OnClick(R.id.term)
        public void editTerm() {
            selectedItemPosition = getAdapterPosition();
            editFlashcardTermDialog.show(flashcards.get(getAdapterPosition()));
        }

        @OnClick(R.id.definition)
        public void editDefinition() {
            selectedItemPosition = getAdapterPosition();
            editFlashcardDefinitionDialog.show(flashcards.get(getAdapterPosition()));
        }

        @OnClick(R.id.delete_flashcard)
        public void deleteFlashcard() {
            deleteFlashcardDialog.show(flashcards.get(getAdapterPosition()).getId());
        }
    }
}
