package com.randomappsinc.simpleflashcards.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;

public class NearbyNameManager {

    public interface Listener {
        void onNameChanged();
    }

    protected PreferencesManager preferencesManager = PreferencesManager.get();
    @NonNull protected Listener listener;
    protected String currentName;
    private MaterialDialog nameSettingDialog;

    public NearbyNameManager(Context context, @NonNull final Listener listener) {
        this.currentName = preferencesManager.getNearbyName();
        this.listener = listener;
        String nearbyNameHint = context.getString(R.string.nearby_name);
        this.nameSettingDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_nearby_name_title)
                .content(R.string.nearby_name_explanation)
                .alwaysCallInputCallback()
                .input(nearbyNameHint, currentName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String setName = input.toString();
                        boolean notEmpty = !setName.trim().isEmpty();
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                    }
                })
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String nearbyName = dialog.getInputEditText().getText().toString().trim();
                        currentName = nearbyName;
                        preferencesManager.setNearbyName(nearbyName);
                        listener.onNameChanged();
                    }
                })
                .negativeText(R.string.cancel)
                .build();
    }

    public String getCurrentName() {
        return currentName;
    }

    public void showNameSetter() {
        nameSettingDialog.show();
    }
}
