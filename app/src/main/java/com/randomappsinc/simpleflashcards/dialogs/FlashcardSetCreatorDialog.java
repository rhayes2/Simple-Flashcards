package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

public class FlashcardSetCreatorDialog {

    public interface Listener {
        void onFlashcardSetCreated(String createdSetName);
    }

    @NonNull private Listener mListener;
    private MaterialDialog mAdderDialog;

    public FlashcardSetCreatorDialog(Context context, @NonNull Listener listener) {
        mListener = listener;
        mAdderDialog = new MaterialDialog.Builder(context)
                .title(R.string.flashcard_set_name)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(context.getString(R.string.flashcard_set_name),
                        "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String setName = input.toString();
                                boolean notEmpty = !setName.trim().isEmpty();
                                boolean notDupe = !DatabaseManager.get().doesSetExist(setName);
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty && notDupe);
                            }
                        })
                .positiveText(R.string.create)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String setName = dialog.getInputEditText().getText().toString().trim();
                        DatabaseManager.get().addFlashcardSet(setName);
                        mListener.onFlashcardSetCreated(setName);
                    }
                })
                .build();
    }

    public void show() {
        mAdderDialog.getInputEditText().setText("");
        mAdderDialog.show();
    }
}
