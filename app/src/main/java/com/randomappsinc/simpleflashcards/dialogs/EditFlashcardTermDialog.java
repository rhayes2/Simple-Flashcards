package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;

public class EditFlashcardTermDialog {

    public interface Listener {
        void onFlashcardTermEdited();
    }

    private MaterialDialog dialog;
    protected Flashcard flashcard;

    public EditFlashcardTermDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.flashcard_edit_term_title)
                .alwaysCallInputCallback()
                .input(context.getString(R.string.term),
                        "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String setName = input.toString();
                                boolean notEmpty = !setName.trim().isEmpty();
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                            }
                        })
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String newTerm = dialog.getInputEditText().getText().toString().trim();
                        DatabaseManager.get().updateFlashcardTerm(flashcard.getId(), newTerm);
                        listener.onFlashcardTermEdited();
                    }
                })
                .build();
        dialog.getInputEditText().setSingleLine(false);
    }

    public void show(Flashcard flashcard) {
        this.flashcard = flashcard;
        dialog.getInputEditText().setText(flashcard.getTerm());
        dialog.show();
    }
}
