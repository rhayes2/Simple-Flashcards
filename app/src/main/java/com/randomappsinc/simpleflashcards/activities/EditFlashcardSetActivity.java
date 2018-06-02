package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.EditFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.dialogs.CreateFlashcardDialog;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class EditFlashcardSetActivity extends StandardActivity {

    @BindView(R.id.set_name_card) View setNameCard;
    @BindView(R.id.flashcard_set_name) EditText flashcardSetName;
    @BindView(R.id.num_flashcards) TextView numFlashcards;
    @BindView(R.id.no_flashcards) TextView noFlashcards;
    @BindView(R.id.flashcards) RecyclerView flashcards;
    @BindView(R.id.add_flashcard) FloatingActionButton addFlashcard;

    protected EditFlashcardsAdapter adapter;
    private int setId;
    private CreateFlashcardDialog createFlashcardDialog;

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
        createFlashcardDialog = new CreateFlashcardDialog(this, flashcardCreatedListener, setId);
        adapter = new EditFlashcardsAdapter(this, setId, noFlashcards, numFlashcards);
        flashcards.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveFlashcardSetName();
    }

    private void saveFlashcardSetName() {
        String newSetName = flashcardSetName.getText().toString().trim();
        if (!newSetName.isEmpty()) {
            DatabaseManager.get().renameSet(setId, newSetName);
        }
    }

    @OnEditorAction(R.id.flashcard_set_name)
    public boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            setNameCard.requestFocus();
            UIUtils.closeKeyboard(this);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFlashcardSetName();
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        createFlashcardDialog.show();
    }

    private final CreateFlashcardDialog.Listener flashcardCreatedListener =
            new CreateFlashcardDialog.Listener() {
                @Override
                public void onFlashcardCreated() {
                    adapter.refreshSet();
                    flashcards.scrollToPosition(adapter.getItemCount() - 1);
                }
            };
}
