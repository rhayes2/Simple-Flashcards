package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;

public class DeleteFlashcardDialog {

    public interface Listener {
        void onFlashcardDeleted();
    }

    private MaterialDialog dialog;

    public DeleteFlashcardDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.flashcard_delete_title)
                .content(R.string.flashcard_delete_message)
                .positiveText(R.string.yes)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onFlashcardDeleted();
                    }
                })
                .build();
    }

    public void show() {
        dialog.show();
    }
}
