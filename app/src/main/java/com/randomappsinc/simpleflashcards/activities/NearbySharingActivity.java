package com.randomappsinc.simpleflashcards.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.managers.NearbyNameManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbySharingActivity extends StandardActivity {

    @BindView(R.id.location_permission_needed) View locationPrompt;
    @BindView(R.id.nearby_name_needed) View nearbyNameNeeded;
    @BindView(R.id.searching) View searching;

    protected String nearbyName;
    protected NearbyNameManager nearbyNameManager;
    private NearbyConnectionsManager nearbyConnectionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_sharing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        nearbyNameManager = new NearbyNameManager(this, nameChangeListener);
        nearbyName = nearbyNameManager.getCurrentName();
        nearbyConnectionsManager = NearbyConnectionsManager.get();
        nearbyConnectionsManager.setListener(connectionsListener);

        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            startSearching();
        } else {
            locationPrompt.setVisibility(View.VISIBLE);
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    1);
        }
    }

    @OnClick(R.id.grant_permission)
    public void askForPermission() {
        PermissionUtils.requestPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                1);
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
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSearching();
        }
    }

    protected void startSearching() {
        locationPrompt.setVisibility(View.GONE);
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

    private final NearbyConnectionsManager.Listener connectionsListener = new NearbyConnectionsManager.Listener() {
        @Override
        public void onNearbyDeviceFound(String endpointId, String endpointName) {
            Toast.makeText(
                    NearbySharingActivity.this,
                    "Found: " + endpointId + " || " + endpointName,
                    Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onNearbyDeviceLost(String endpointId) {
            Toast.makeText(
                    NearbySharingActivity.this,
                    "Lost: " + endpointId,
                    Toast.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        nearbyConnectionsManager.shutdown();
    }
}
