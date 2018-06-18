package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;

import butterknife.ButterKnife;

public class NearbyFlashcardsTransferActivity extends StandardActivity {

    private NearbyConnectionsManager nearbyConnectionsManager = NearbyConnectionsManager.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_flashcards_transfer);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        setTitle(getString(R.string.connected_to, nearbyConnectionsManager.getOtherSideName()));

        nearbyConnectionsManager.setPostConnectionListener(postConnectionListener);
    }

    private final NearbyConnectionsManager.PostConnectionListener postConnectionListener =
            new NearbyConnectionsManager.PostConnectionListener() {
                @Override
                public void onDisconnect() {
                    finish();
                }
            };

    @Override
    public void finish() {
        super.finish();
        nearbyConnectionsManager.disconnect();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
