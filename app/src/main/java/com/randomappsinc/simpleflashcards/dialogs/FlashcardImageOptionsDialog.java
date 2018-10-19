package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;

public class FlashcardImageOptionsDialog {

    public interface Listener {
        void onFullViewRequested();

        void onFlashcardImageChangeRequested();

        void onFlashcardImageDeleted();
    }

    protected MaterialDialog optionsDialog;
    protected MaterialDialog confirmDeletionDialog;

    public FlashcardImageOptionsDialog(Context context, final Listener listener) {
        String[] options = context.getResources().getStringArray(R.array.flashcard_image_options);
        optionsDialog = new MaterialDialog.Builder(context)
                .items(options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(
                            MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                listener.onFullViewRequested();
                                break;
                            case 1:
                                listener.onFlashcardImageChangeRequested();
                                break;
                            case 2:
                                confirmDeletionDialog.show();
                                break;
                            default:
                                throw new IllegalStateException("Unsupported index clicked for image options");
                        }
                    }
                })
                .build();
        confirmDeletionDialog = new MaterialDialog.Builder(context)
                .title(R.string.delete_image_confirmation_title)
                .content(R.string.delete_image_confirmation_body)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onFlashcardImageDeleted();
                    }
                })
                .build();
    }

    public void show() {
        optionsDialog.show();
    }
}
