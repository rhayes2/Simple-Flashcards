package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;

public class QuitQuizDialog {

    public interface Listener {
        void onQuitQuizConfirmed();
    }

    private MaterialDialog dialog;

    public QuitQuizDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_quiz_exit)
                .content(R.string.confirm_quiz_exit_body)
                .positiveText(R.string.yes)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onQuitQuizConfirmed();
                    }
                })
                .build();
    }

    public void show() {
        dialog.show();
    }
}
