package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;

public class EditFlashcardDefinitionDialog {

    public interface Listener {
        void onFlashcardDefinitionEdited(String newDefinition);
    }

    private MaterialDialog dialog;

    public EditFlashcardDefinitionDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.flashcard_edit_definition_title)
                .alwaysCallInputCallback()
                .input(context.getString(R.string.definition),
                        "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String definition = input.toString();
                                boolean notEmpty = !definition.trim().isEmpty();
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                            }
                        })
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String newDefinition = dialog.getInputEditText().getText().toString().trim();
                        listener.onFlashcardDefinitionEdited(newDefinition);
                    }
                })
                .build();
        dialog.getInputEditText().setSingleLine(false);
    }

    public void show(Flashcard flashcard) {
        dialog.getInputEditText().setText(flashcard.getDefinition());
        dialog.show();
    }
}
