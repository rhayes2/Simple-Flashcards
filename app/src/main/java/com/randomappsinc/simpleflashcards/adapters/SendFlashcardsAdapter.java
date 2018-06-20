package com.randomappsinc.simpleflashcards.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.FlashcardSetTransferState;
import com.randomappsinc.simpleflashcards.models.FlashcardSetForTransfer;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.MyApplication;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendFlashcardsAdapter extends RecyclerView.Adapter<SendFlashcardsAdapter.FlashcardSetViewHolder> {

    public interface Listener {
        void onSendFlashcardSet(FlashcardSet flashcardSet);
    }

    protected Listener listener;
    protected List<FlashcardSetForTransfer> flashcardSets;

    public SendFlashcardsAdapter(List<FlashcardSet> flashcardSetList, Listener listener) {
        flashcardSets = new ArrayList<>();
        for (FlashcardSet original : flashcardSetList) {
            flashcardSets.add(new FlashcardSetForTransfer(original));
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlashcardSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.send_flashcard_set_cell,
                parent,
                false);
        return new FlashcardSetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardSetViewHolder holder, int position) {
        holder.loadFlashcardSet(position);
    }

    @Override
    public int getItemCount() {
        return flashcardSets.size();
    }

    class FlashcardSetViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.set_name) TextView setName;
        @BindView(R.id.num_flashcards) TextView numFlashcardsText;
        @BindView(R.id.send) View send;
        @BindView(R.id.sending) View sending;
        @BindView(R.id.sent) View sent;

        FlashcardSetViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadFlashcardSet(int position) {
            FlashcardSetForTransfer flashcardSetForTransfer = flashcardSets.get(position);
            FlashcardSet flashcardSet = flashcardSetForTransfer.getFlashcardSet();
            setName.setText(flashcardSet.getName());
            int numFlashcards = flashcardSet.getFlashcards().size();
            numFlashcardsText.setText(numFlashcards == 1
                    ? StringUtils.getString(R.string.one_flashcard)
                    : MyApplication.getAppContext().getString(R.string.x_flashcards, numFlashcards));

            switch (flashcardSetForTransfer.getTransferState()) {
                case FlashcardSetTransferState.NOT_YET_SENT:
                    sending.setVisibility(View.GONE);
                    sent.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                    break;
                case FlashcardSetTransferState.SENDING:
                    send.setVisibility(View.GONE);
                    sent.setVisibility(View.GONE);
                    sending.setVisibility(View.VISIBLE);
                    break;
                case FlashcardSetTransferState.SENT:
                    send.setVisibility(View.GONE);
                    sending.setVisibility(View.GONE);
                    sent.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @OnClick(R.id.send)
        public void sendFlashcardSet() {
            listener.onSendFlashcardSet(flashcardSets.get(getAdapterPosition()).getFlashcardSet());
        }
    }
}
