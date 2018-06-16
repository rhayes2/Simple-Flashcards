package com.randomappsinc.simpleflashcards.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.NearbyNameManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbySharingActivity extends StandardActivity {

    @BindView(R.id.location_permission_needed) View locationPrompt;
    @BindView(R.id.nearby_name_needed) View nearbyNameNeeded;
    @BindView(R.id.searching) View searching;

    protected String nearbyName;
    protected NearbyNameManager nearbyNameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_sharing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        nearbyNameManager = new NearbyNameManager(this, nameChangeListener);
        nearbyName = nearbyNameManager.getCurrentName();

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

    @OnClick(R.id.set_nearby_name_button)
    public void setNearbyName() {
        nearbyNameManager.showNameSetter();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSearching();
        }
    }

    private void startSearching() {
        locationPrompt.setVisibility(View.GONE);
        if (TextUtils.isEmpty(nearbyName)) {
            nearbyNameNeeded.setVisibility(View.VISIBLE);
            nearbyNameManager.showNameSetter();
        } else {
            searching.setVisibility(View.VISIBLE);
        }
    }

    private final NearbyNameManager.Listener nameChangeListener = new NearbyNameManager.Listener() {
        @Override
        public void onNameChanged() {
            nearbyName = nearbyNameManager.getCurrentName();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nearby_sharing, menu);
        UIUtils.loadMenuIcon(menu, R.id.set_nearby_name, IoniconsIcons.ion_android_person, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_nearby_name:
                nearbyNameManager.showNameSetter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
