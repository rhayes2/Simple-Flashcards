package com.randomappsinc.simpleflashcards.dialogs;


import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;

public class EditFlashcardSetNameDialog {

    public interface Listener {
        void onFlashcardSetRename(String newSetName);
    }

    private MaterialDialog dialog;

    public EditFlashcardSetNameDialog(Context context, String initialSetName, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.rename_flashcard_set_title)
                .alwaysCallInputCallback()
                .input(context.getString(R.string.flashcard_set_name),
                        initialSetName,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String title = input.toString();
                                boolean notEmpty = !title.trim().isEmpty();
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                            }
                        })
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String newTitle = dialog.getInputEditText().getText().toString().trim();
                        listener.onFlashcardSetRename(newTitle);
                    }
                })
                .build();
    }

    public void show() {
        dialog.show();
    }
}
