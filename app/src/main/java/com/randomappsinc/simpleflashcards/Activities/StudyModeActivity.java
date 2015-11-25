package com.randomappsinc.simpleflashcards.Activities;

import android.os.Bundle;
import android.view.Menu;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.R;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class StudyModeActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.study_mode_menu, menu);
        menu.findItem(R.id.random_flashcard).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_random)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.previous_flashcard).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_arrow_left)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.next_flashcard).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_arrow_right)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }
}
