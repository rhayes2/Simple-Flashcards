package com.randomappsinc.simpleflashcards.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.Adapters.FlashcardsAdapter;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class EditFlashcardSetActivity extends StandardActivity {
    @Bind(R.id.set_name) TextView flashcardSetTitle;
    @Bind(R.id.no_flashcards) TextView noFlashcards;
    @Bind(R.id.flashcards) ListView flashcards;
    @Bind(R.id.add_flashcard) FloatingActionButton addFlashcard;
    @BindString(R.string.new_flashcard_set_name) String newSetName;

    private FlashcardsAdapter adapter;
    private String setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flashcard_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setName = getIntent().getStringExtra(MainActivity.FLASHCARD_SET_KEY);
        flashcardSetTitle.setText(setName);

        addFlashcard.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus).colorRes(R.color.white));
        adapter = new FlashcardsAdapter(this, setName, noFlashcards);
        flashcards.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshSet();
    }

    @OnItemClick(R.id.flashcards)
    public void onFlashcardClick(int position) {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(MainActivity.FLASHCARD_SET_KEY, setName);
        intent.putExtra(FlashcardFormActivity.QUESTION_KEY, adapter.getItem(position).getQuestion());
        intent.putExtra(FlashcardFormActivity.ANSWER_KEY, adapter.getItem(position).getAnswer());
        intent.putExtra(FlashcardFormActivity.UPDATE_MODE_KEY, true);
        startActivity(intent);
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(MainActivity.FLASHCARD_SET_KEY, setName);
        startActivity(intent);
    }

    public void showRenameDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.rename_flashcard_set)
                .input(newSetName, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                DatabaseManager.get().doesSetExist(input.toString()));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newSetName = dialog.getInputEditText().getText().toString();
                            DatabaseManager.get().renameSet(setName, newSetName);
                            setName = newSetName;
                            flashcardSetTitle.setText(setName);
                        }
                    }
                })
                .show();
    }

    private void showDeleteDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.flashcard_set_delete_title)
                .content(R.string.flashcard_set_delete_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().deleteFlashcardSet(setName);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flashcard_set_menu, menu);
        menu.findItem(R.id.rename_set).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_edit)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.delete_set).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_trash_o)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_set:
                showRenameDialog();
                return true;
            case R.id.delete_set:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
