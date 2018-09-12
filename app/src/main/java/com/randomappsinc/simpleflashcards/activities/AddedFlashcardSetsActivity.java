package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.AddedFlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Shows an overview of "imported" flashcard sets, either from nearby sharing or restoration
 */
public class AddedFlashcardSetsActivity extends StandardActivity {

    @BindView(R.id.added_sets) RecyclerView addedSetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.added_flashcard_sets);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        int[] setIds = getIntent().getIntArrayExtra(Constants.ADDED_SET_IDS_KEY);
        AddedFlashcardSetsAdapter adapter = new AddedFlashcardSetsAdapter(setIds);
        addedSetsList.setAdapter(adapter);
        addedSetsList.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
