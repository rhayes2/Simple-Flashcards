package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.MyApplication;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

public class ConfirmConnectionDialog {

    public interface Listener {
        void onConnectionAccepted();

        void onConnectionRejected();
    }

    protected MaterialDialog dialog;

    public ConfirmConnectionDialog(Context context, @NonNull final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .title("")
                .content("")
                .positiveText(R.string.accept)
                .negativeText(R.string.reject)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onConnectionAccepted();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onConnectionRejected();
                    }
                })
                .build();
    }

    public void show(ConnectionInfo connectionInfo) {
        Context context = MyApplication.getAppContext();
        String deviceText = StringUtils.getSaneDeviceString(connectionInfo.getEndpointName());
        dialog.setTitle(connectionInfo.isIncomingConnection()
                ? context.getString(R.string.x_would_like_to_connect, deviceText)
                : context.getString(R.string.connecting_to_x, deviceText));
        dialog.setContent(connectionInfo.getAuthenticationToken());
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
