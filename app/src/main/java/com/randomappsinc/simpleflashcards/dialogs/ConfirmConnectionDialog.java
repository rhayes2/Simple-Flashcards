package com.randomappsinc.simpleflashcards.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.MyApplication;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmConnectionDialog {

    public interface Listener {
        void onConnectionAccepted();

        void onConnectionRejected();
    }

    @BindView(R.id.connection_prompt) TextView title;
    @BindView(R.id.authentication_token) TextView authToken;

    protected MaterialDialog dialog;

    public ConfirmConnectionDialog(Context context, @NonNull final Listener listener) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.confirm_connection, null);
        ButterKnife.bind(this, contentView);
        dialog = new MaterialDialog.Builder(context)
                .customView(contentView, true)
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
                .cancelable(false)
                .build();
    }

    public void show(ConnectionInfo connectionInfo) {
        Context context = MyApplication.getAppContext();
        String deviceText = StringUtils.getSaneDeviceString(connectionInfo.getEndpointName());
        title.setText(connectionInfo.isIncomingConnection()
                ? context.getString(R.string.x_would_like_to_connect, deviceText)
                : context.getString(R.string.connecting_to_x, deviceText));
        authToken.setText(connectionInfo.getAuthenticationToken());
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
