package com.randomappsinc.simpleflashcards.activities;

import com.randomappsinc.simpleflashcards.R;

/**
 * Shows an overview of "imported" flashcard sets, either from nearby sharing or restoration
 */
public class AddedFlashcardSetsActivity extends StandardActivity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
