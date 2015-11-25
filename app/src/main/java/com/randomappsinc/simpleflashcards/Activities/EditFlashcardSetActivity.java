package com.randomappsinc.simpleflashcards.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.Adapters.FlashcardsAdapter;
import com.randomappsinc.simpleflashcards.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class EditFlashcardSetActivity extends StandardActivity {
    public static final String FLASHCARD_SET_KEY = "flashcardSet";

    @Bind(R.id.no_flashcards) TextView noFlashcards;
    @Bind(R.id.flashcards) ListView flashcards;
    @Bind(R.id.add_flashcard) FloatingActionButton addFlashcard;

    private FlashcardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flashcard_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        String flashcardSetName = getIntent().getStringExtra(FLASHCARD_SET_KEY);
        setTitle(getString(R.string.editing) + flashcardSetName);

        addFlashcard.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus).colorRes(R.color.white));
        adapter = new FlashcardsAdapter(this, flashcardSetName, noFlashcards);
        flashcards.setAdapter(adapter);
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard(View view) {
        startActivity(new Intent(this, FlashcardFormActivity.class));
    }
}
