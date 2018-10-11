package com.randomappsinc.simpleflashcards.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import com.randomappsinc.simpleflashcards.dialogs.DeleteFlashcardDialog;
import com.randomappsinc.simpleflashcards.dialogs.EditFlashcardDefinitionDialog;
import com.randomappsinc.simpleflashcards.dialogs.EditFlashcardTermDialog;
import com.randomappsinc.simpleflashcards.dialogs.FlashcardImageOptionsDialog;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class EditFlashcardSetActivity extends StandardActivity {

    private static final int IMAGE_FILE_REQUEST_CODE = 1;

    @BindView(R.id.set_name_card) View setNameCard;
    @BindView(R.id.flashcard_set_name) EditText flashcardSetName;
    @BindView(R.id.num_flashcards) TextView numFlashcards;
    @BindView(R.id.no_flashcards) TextView noFlashcards;
    @BindView(R.id.flashcards) RecyclerView flashcards;
    @BindView(R.id.add_flashcard) FloatingActionButton addFlashcard;

    protected EditFlashcardsAdapter adapter;
    protected int setId;
    private CreateFlashcardDialog createFlashcardDialog;
    protected DeleteFlashcardDialog deleteFlashcardDialog;
    protected EditFlashcardTermDialog editFlashcardTermDialog;
    protected EditFlashcardDefinitionDialog editFlashcardDefinitionDialog;
    protected FlashcardImageOptionsDialog flashcardImageOptionsDialog;
    protected int currentlySelectedFlashcardId;
    protected DatabaseManager databaseManager = DatabaseManager.get();

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
        createFlashcardDialog = new CreateFlashcardDialog(this, flashcardCreatedListener);
        deleteFlashcardDialog = new DeleteFlashcardDialog(this, flashcardDeleteListener);
        editFlashcardTermDialog = new EditFlashcardTermDialog(this, flashcardTermEditListener);
        editFlashcardDefinitionDialog = new EditFlashcardDefinitionDialog(
                this, flashcardDefinitionEditListener);
        flashcardImageOptionsDialog = new FlashcardImageOptionsDialog(
                this, flashcardOptionsListener);
        adapter = new EditFlashcardsAdapter(flashcardEditingListener, setId, noFlashcards, numFlashcards);
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
    protected void onPause() {
        super.onPause();
        saveFlashcardSetName();
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        createFlashcardDialog.show();
    }

    protected void searchForImageFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_FILE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == IMAGE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null && resultData.getData() != null) {
                String uriString = resultData.getData().toString();
                databaseManager.updateFlashcardTermImageUrl(currentlySelectedFlashcardId, uriString);
                adapter.onTermImageUpdated(uriString);
            }
        }
    }

    private final CreateFlashcardDialog.Listener flashcardCreatedListener =
            new CreateFlashcardDialog.Listener() {
                @Override
                public void onFlashcardCreated(String term, String definition) {
                    databaseManager.addFlashcard(setId, term, definition);
                    adapter.refreshSet();
                    flashcards.scrollToPosition(adapter.getItemCount() - 1);
                }
            };

    private final EditFlashcardsAdapter.Listener flashcardEditingListener =
            new EditFlashcardsAdapter.Listener() {
                @Override
                public void onEditTerm(Flashcard flashcard) {
                    currentlySelectedFlashcardId = flashcard.getId();
                    editFlashcardTermDialog.show(flashcard);
                }

                @Override
                public void onEditDefinition(Flashcard flashcard) {
                    currentlySelectedFlashcardId = flashcard.getId();
                    editFlashcardDefinitionDialog.show(flashcard);
                }

                @Override
                public void onDeleteFlashcard(Flashcard flashcard) {
                    currentlySelectedFlashcardId = flashcard.getId();
                    deleteFlashcardDialog.show();
                }

                @Override
                public void onImageClicked(Flashcard flashcard) {
                    currentlySelectedFlashcardId = flashcard.getId();
                    flashcardImageOptionsDialog.show();
                }

                @Override
                public void onAddImageClicked(Flashcard flashcard) {
                    currentlySelectedFlashcardId = flashcard.getId();
                    searchForImageFile();
                }
            };

    private final DeleteFlashcardDialog.Listener flashcardDeleteListener =
            new DeleteFlashcardDialog.Listener() {
                @Override
                public void onFlashcardDeleted() {
                    databaseManager.deleteFlashcard(currentlySelectedFlashcardId);
                    adapter.onFlashcardDeleted();
                }
            };

    private final EditFlashcardTermDialog.Listener flashcardTermEditListener =
            new EditFlashcardTermDialog.Listener() {
                @Override
                public void onFlashcardTermEdited(String newTerm) {
                    databaseManager.updateFlashcardTerm(currentlySelectedFlashcardId, newTerm);
                    adapter.onFlashcardTermEdited(newTerm);
                }
            };

    private final EditFlashcardDefinitionDialog.Listener flashcardDefinitionEditListener =
            new EditFlashcardDefinitionDialog.Listener() {
                @Override
                public void onFlashcardDefinitionEdited(String newDefinition) {
                    databaseManager.updateFlashcardDefinition(currentlySelectedFlashcardId, newDefinition);
                    adapter.onFlashcardDefinitionEdited(newDefinition);
                }
            };

    private final FlashcardImageOptionsDialog.Listener flashcardOptionsListener =
            new FlashcardImageOptionsDialog.Listener() {
                @Override
                public void onFlashcardImageChangeRequested() {
                    searchForImageFile();
                }

                @Override
                public void onFlashcardImageDeleted() {
                    databaseManager.updateFlashcardTermImageUrl(currentlySelectedFlashcardId, null);
                    adapter.onTermImageUpdated(null);
                }
            };
}
