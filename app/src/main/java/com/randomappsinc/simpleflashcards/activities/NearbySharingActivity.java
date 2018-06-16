package com.randomappsinc.simpleflashcards.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbySharingActivity extends StandardActivity {

    @BindView(R.id.location_permission_needed) View locationPrompt;
    @BindView(R.id.searching) View searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_sharing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            startSearching();
        } else {
            locationPrompt.setVisibility(View.VISIBLE);
            PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, 1);
        }
    }

    @OnClick(R.id.grant_permission)
    public void askForPermission() {
        PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSearching();
        }
    }

    private void startSearching() {
        locationPrompt.setVisibility(View.GONE);
        searching.setVisibility(View.VISIBLE);
    }
}
