package com.randomappsinc.simpleflashcards.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.DialogUtil;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFlashcardSetActivity extends StandardActivity {

    // Intent codes
    private static final int IMAGE_FILE_REQUEST_CODE = 1;

    // Permission codes
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;

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
        setTitle(DatabaseManager.get().getSetName(setId));
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

        PreferencesManager preferencesManager = new PreferencesManager(this);
        if (preferencesManager.shouldShowRenameFlashcardSetInstructions()) {
            DialogUtil.showDialogWithIconTextBody(
                    this,
                    R.string.rename_set_instructions,
                    R.string.rename_set_instructions_title,
                    android.R.string.ok);
        }
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        createFlashcardDialog.show();
    }

    protected void verifyReadExternalStoragePermission() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            searchForImageFile();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && requestCode == IMAGE_FILE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            if (resultData != null && resultData.getData() != null) {
                Uri uri = resultData.getData();

                // Persist ability to read from this file
                int takeFlags = resultData.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                String uriString = uri.toString();
                databaseManager.updateFlashcardTermImageUrl(currentlySelectedFlashcardId, uriString);
                adapter.onTermImageUpdated(uriString);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            searchForImageFile();
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
                    verifyReadExternalStoragePermission();
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
                public void onFullViewRequested() {
                    Flashcard flashcard = adapter.getCurrentlyChosenFlashcard();
                    Intent intent = new Intent(
                            EditFlashcardSetActivity.this,
                            PictureFullViewActivity.class)
                            .putExtra(Constants.IMAGE_URL_KEY, flashcard.getTermImageUrl())
                            .putExtra(Constants.CAPTION_KEY, flashcard.getTerm());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, 0);
                }

                @Override
                public void onFlashcardImageChangeRequested() {
                    verifyReadExternalStoragePermission();
                }

                @Override
                public void onFlashcardImageDeleted() {
                    databaseManager.updateFlashcardTermImageUrl(currentlySelectedFlashcardId, null);
                    adapter.onTermImageUpdated(null);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_set, menu);
        UIUtils.loadMenuIcon(menu, R.id.rename_flashcard_set, IoniconsIcons.ion_edit, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_flashcard_set:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
