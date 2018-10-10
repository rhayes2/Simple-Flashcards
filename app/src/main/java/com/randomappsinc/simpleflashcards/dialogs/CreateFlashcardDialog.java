package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;

public class CreateFlashcardDialog {

    public interface Listener {
        void onFlashcardCreated(String term, String definition);
    }

    protected MaterialDialog termDialog;
    protected MaterialDialog definitionDialog;

    public CreateFlashcardDialog(Context context, @NonNull final Listener listener) {
        termDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_flashcard_term)
                .alwaysCallInputCallback()
                .input(context.getString(R.string.term),
                        "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String term = input.toString();
                                boolean notEmpty = !term.trim().isEmpty();
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                            }
                        })
                .positiveText(R.string.next)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        definitionDialog.show();
                    }
                })
                .build();
        termDialog.getInputEditText().setSingleLine(false);
        definitionDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_flashcard_definition)
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
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .neutralText(R.string.back)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String term = termDialog.getInputEditText().getText().toString().trim();
                        String definition = dialog.getInputEditText().getText().toString().trim();
                        listener.onFlashcardCreated(term, definition);
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        termDialog.show();
                    }
                })
                .build();
        definitionDialog.getInputEditText().setSingleLine(false);
    }

    public void show() {
        termDialog.getInputEditText().setText("");
        definitionDialog.getInputEditText().setText("");
        termDialog.show();
    }
}
