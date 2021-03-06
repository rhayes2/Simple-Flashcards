package com.randomappsinc.simpleflashcards.activities;

import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.NearbyDevicesAdapter;
import com.randomappsinc.simpleflashcards.dialogs.ConfirmConnectionDialog;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.managers.NearbyNameManager;
import com.randomappsinc.simpleflashcards.models.NearbyDevice;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbySharingActivity extends StandardActivity {

    @BindView(R.id.permissions_needed) View permissionsPrompt;
    @BindView(R.id.nearby_name_needed) View nearbyNameNeeded;
    @BindView(R.id.searching) View searching;
    @BindView(R.id.skeleton_devices_list) View skeletonDevicesList;
    @BindView(R.id.devices_list) RecyclerView devicesList;

    protected String nearbyName;
    protected NearbyNameManager nearbyNameManager;
    protected NearbyConnectionsManager nearbyConnectionsManager;
    protected NearbyDevicesAdapter nearbyDevicesAdapter;
    protected MaterialDialog requestingConnectionDialog;
    protected ConfirmConnectionDialog confirmConnectionDialog;
    protected MaterialDialog waitingForAcceptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_sharing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        nearbyNameManager = new NearbyNameManager(this, nameChangeListener);
        nearbyName = nearbyNameManager.getCurrentName();
        nearbyConnectionsManager = NearbyConnectionsManager.get();
        nearbyConnectionsManager.initialize(this);
        nearbyConnectionsManager.setPreConnectionListener(preConnectionListener);
        confirmConnectionDialog = new ConfirmConnectionDialog(this, connectionChoiceListener);

        nearbyDevicesAdapter = new NearbyDevicesAdapter(deviceChoiceListener);
        devicesList.setAdapter(nearbyDevicesAdapter);

        requestingConnectionDialog = new MaterialDialog.Builder(this)
                .content(R.string.requesting_connection)
                .progress(true, 0)
                .cancelable(false)
                .build();

        waitingForAcceptDialog = new MaterialDialog.Builder(this)
                .content(R.string.waiting_for_accept)
                .progress(true, 0)
                .cancelable(false)
                .build();

        if (arePermissionsGranted()) {
            startSearching();
        } else {
            permissionsPrompt.setVisibility(View.VISIBLE);
            requestPermissions();
        }
    }

    private boolean arePermissionsGranted() {
        return PermissionUtils.isPermissionGranted(permission.ACCESS_COARSE_LOCATION, this)
                && PermissionUtils.isPermissionGranted(permission.WRITE_EXTERNAL_STORAGE, this);
    }

    private void requestPermissions() {
        String[] permissions = new String[]{
                permission.ACCESS_COARSE_LOCATION,
                permission.WRITE_EXTERNAL_STORAGE};
        PermissionUtils.requestPermissions(this, permissions);
    }

    @OnClick(R.id.grant_permissions)
    public void onPermissionButtonClick() {
        requestPermissions();
    }

    @OnClick(R.id.set_nearby_name_button)
    public void setNearbyName() {
        nearbyNameManager.showNameSetter();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (arePermissionsGranted()) {
            startSearching();
        }
    }

    protected void startSearching() {
        permissionsPrompt.setVisibility(View.GONE);
        if (TextUtils.isEmpty(nearbyName)) {
            nearbyNameNeeded.setVisibility(View.VISIBLE);
            nearbyNameManager.showNameSetter();
        } else {
            nearbyNameNeeded.setVisibility(View.GONE);
            searching.setVisibility(View.VISIBLE);
            nearbyConnectionsManager.startAdvertisingAndDiscovering(this);
        }
    }

    private final NearbyNameManager.Listener nameChangeListener = new NearbyNameManager.Listener() {
        @Override
        public void onNameChanged() {
            nearbyName = nearbyNameManager.getCurrentName();
            startSearching();
        }
    };

    private final NearbyConnectionsManager.PreConnectionListener preConnectionListener =
            new NearbyConnectionsManager.PreConnectionListener() {
                @Override
                public void onNearbyDeviceFound(NearbyDevice device) {
                    nearbyDevicesAdapter.addNearbyDevice(device);
                    skeletonDevicesList.setVisibility(View.GONE);
                }

                @Override
                public void onNearbyDeviceLost(String endpointId) {
                    nearbyDevicesAdapter.removeNearbyDevice(endpointId);
                    if (nearbyDevicesAdapter.getItemCount() == 0) {
                        skeletonDevicesList.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onConnectionRequest(ConnectionInfo connectionInfo) {
                    requestingConnectionDialog.dismiss();
                    confirmConnectionDialog.show(connectionInfo, NearbySharingActivity.this);
                }

                @Override
                public void onConnectionFailed() {
                    requestingConnectionDialog.dismiss();
                    confirmConnectionDialog.dismiss();
                    waitingForAcceptDialog.dismiss();
                    UIUtils.showLongToast(
                            R.string.connection_confirmation_failed,
                            NearbySharingActivity.this);
                }

                @Override
                public void onConnectionRejected() {
                    requestingConnectionDialog.dismiss();
                    confirmConnectionDialog.dismiss();
                    waitingForAcceptDialog.dismiss();
                    UIUtils.showLongToast(
                            R.string.connection_rejected,
                            NearbySharingActivity.this);
                }

                @Override
                public void onConnectionSuccessful() {
                    waitingForAcceptDialog.dismiss();
                    openNearbyFlashcardsTransferPage();
                    UIUtils.showLongToast(R.string.connection_successful, NearbySharingActivity.this);
                }

                @Override
                public void onAdvertisingFailed() {
                    UIUtils.showLongToast(
                            R.string.advertising_fail,
                            NearbySharingActivity.this);
                }

                @Override
                public void onDiscoveryFailed() {
                    UIUtils.showLongToast(
                            R.string.discovery_fail,
                            NearbySharingActivity.this);
                }
            };

    private final NearbyDevicesAdapter.Listener deviceChoiceListener = new NearbyDevicesAdapter.Listener() {
        @Override
        public void onNearbyDeviceChosen(NearbyDevice device) {
            requestingConnectionDialog.show();
            nearbyConnectionsManager.requestConnection(device.getEndpointId());
        }
    };

    private final ConfirmConnectionDialog.Listener connectionChoiceListener =
            new ConfirmConnectionDialog.Listener() {
                @Override
                public void onConnectionAccepted() {
                    waitingForAcceptDialog.show();
                    nearbyConnectionsManager.acceptConnection();
                }

                @Override
                public void onConnectionRejected() {
                    nearbyConnectionsManager.rejectConnection();
                }
            };

    protected void openNearbyFlashcardsTransferPage() {
        Intent intent = new Intent(this, NearbyFlashcardsTransferActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nearbyConnectionsManager.shutdown();
    }
}
