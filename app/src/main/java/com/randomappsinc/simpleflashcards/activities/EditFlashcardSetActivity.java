package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsOverviewAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class EditFlashcardSetActivity extends StandardActivity {

    @BindView(R.id.flashcard_set_name) EditText flashcardSetName;
    @BindView(R.id.num_flashcards) TextView numFlashcards;
    @BindView(R.id.no_flashcards) TextView noFlashcards;
    @BindView(R.id.flashcards) ListView flashcards;
    @BindView(R.id.add_flashcard) FloatingActionButton addFlashcard;

    @BindString(R.string.one_flashcard) String oneFlashcard;
    @BindString(R.string.x_flashcards) String xFlashcards;

    private FlashcardsOverviewAdapter adapter;
    private int setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flashcard_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        flashcardSetName.setText(DatabaseManager.get().getSetName(setId));

        addFlashcard.setImageDrawable(
                new IconDrawable(this, IoniconsIcons.ion_android_add)
                        .colorRes(R.color.white));
        adapter = new FlashcardsOverviewAdapter(this, setId, noFlashcards);
        flashcards.setAdapter(adapter);

        int flashcardsCount = adapter.getCount();
        String numFlashcardsText = flashcardsCount == 1
                ? oneFlashcard
                : String.format(Locale.getDefault(), xFlashcards, flashcardsCount);
        numFlashcards.setText(numFlashcardsText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshSet();
    }

    @OnItemClick(R.id.flashcards)
    public void onFlashcardClick(int position) {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(FlashcardFormActivity.FLASHCARD_ID_KEY, adapter.getItem(position).getId());
        intent.putExtra(FlashcardFormActivity.UPDATE_MODE_KEY, true);
        startActivity(intent);
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(Constants.FLASHCARD_SET_ID_KEY, setId);
        startActivity(intent);
    }
}
