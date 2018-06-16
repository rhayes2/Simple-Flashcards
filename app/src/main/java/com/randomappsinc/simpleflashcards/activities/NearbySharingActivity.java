package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;

import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

public class NearbySharingActivity extends StandardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_sharing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }
}
