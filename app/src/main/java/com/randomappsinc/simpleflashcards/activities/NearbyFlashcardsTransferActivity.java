package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.NearbyFlashcardsTabsAdapter;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyFlashcardsTransferActivity extends StandardActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nearby_flashcards_tabs) TabLayout tabs;
    @BindView(R.id.nearby_flashcards_pager) ViewPager viewPager;

    private NearbyConnectionsManager nearbyConnectionsManager = NearbyConnectionsManager.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_flashcards_transfer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());
        setTitle(getString(R.string.connected_to, nearbyConnectionsManager.getOtherSideName()));

        nearbyConnectionsManager.setPostConnectionListener(postConnectionListener);
        viewPager.setAdapter(new NearbyFlashcardsTabsAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(viewPager);
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
