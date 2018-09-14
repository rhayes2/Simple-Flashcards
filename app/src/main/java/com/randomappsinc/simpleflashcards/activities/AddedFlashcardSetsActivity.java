package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.AddedFlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.models.FlashcardSetPreview;
import com.randomappsinc.simpleflashcards.views.SimpleDividerItemDecoration;

import java.util.List;

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

        List<FlashcardSetPreview> sets = getIntent().getParcelableArrayListExtra(Constants.ADDED_SET_PREVIEWS);
        AddedFlashcardSetsAdapter adapter = new AddedFlashcardSetsAdapter(listClickListener, sets);
        addedSetsList.setAdapter(adapter);
        addedSetsList.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private final AddedFlashcardSetsAdapter.Listener listClickListener = new AddedFlashcardSetsAdapter.Listener() {
        @Override
        public void onCellClicked(FlashcardSetPreview setPreview) {
            Intent intent = new Intent(AddedFlashcardSetsActivity.this, PlainSetViewActivity.class)
                    .putExtra(Constants.SET_PREVIEW_KEY, setPreview);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
