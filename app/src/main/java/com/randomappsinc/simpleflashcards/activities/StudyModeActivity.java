package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsBrowsingAdapter;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudyModeActivity extends StandardActivity {

    @BindView(R.id.flashcards_pager) ViewPager flashcardsPager;

    private FlashcardsBrowsingAdapter flashcardsBrowsingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        int setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        setTitle(DatabaseManager.get().getSetName(setId));

        flashcardsBrowsingAdapter = new FlashcardsBrowsingAdapter(getSupportFragmentManager(), setId);
        flashcardsPager.setAdapter(flashcardsBrowsingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.study_mode_menu, menu);
        menu.findItem(R.id.shuffle).setIcon(
                new IconDrawable(this, IoniconsIcons.ion_shuffle)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shuffle:
                flashcardsBrowsingAdapter.shuffle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
