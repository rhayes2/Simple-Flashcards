package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

public class DeleteFlashcardSetDialog {

    public interface Listener {
        void onFlashcardSetDeleted();
    }

    private MaterialDialog dialog;
    private int flashcardSetId;

    public DeleteFlashcardSetDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.flashcard_set_delete_title)
                .content(R.string.flashcard_set_delete_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().deleteFlashcardSet(flashcardSetId);
                        listener.onFlashcardSetDeleted();
                    }
                })
                .build();
    }

    public void show(int flashcardSetId) {
        this.flashcardSetId = flashcardSetId;
        dialog.show();
    }
}
