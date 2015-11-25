package com.randomappsinc.simpleflashcards.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.Adapters.FlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.Persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.Utils.Utils;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

public class MainActivity extends StandardActivity {
    public static final String FLASHCARD_SET_KEY = "flashcardSet";

    @Bind(R.id.set_name) EditText setName;
    @Bind(R.id.add_icon) ImageView addButton;
    @Bind(R.id.flashcard_sets) ListView sets;
    @Bind(R.id.no_sets) TextView noSets;
    @Bind(R.id.parent) View parent;
    @BindString(R.string.new_flashcard_set_name) String newSetName;

    private Activity activity;
    private FlashcardSetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addButton.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus)
                .colorRes(R.color.white)
                .actionBarSize());

        if (PreferencesManager.get().isFirstTimeUser()) {
            PreferencesManager.get().rememberWelcome();
            new MaterialDialog.Builder(this)
                    .title(R.string.welcome)
                    .content(R.string.ask_for_help)
                    .positiveText(android.R.string.yes)
                    .show();
        }

        adapter = new FlashcardSetsAdapter(this, noSets);
        sets.setAdapter(adapter);
    }

    @OnItemClick(R.id.flashcard_sets)
    public void onFlashcardSetClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, StudyModeActivity.class);
        intent.putExtra(FLASHCARD_SET_KEY, adapter.getItem(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnItemLongClick(R.id.flashcard_sets)
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        String title = getString(R.string.options_for) + adapter.getItem(position);

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        IconDrawable editIcon = new IconDrawable(this, FontAwesomeIcons.fa_edit).colorRes(R.color.dark_gray);
        IconDrawable deleteIcon = new IconDrawable(this, FontAwesomeIcons.fa_remove).colorRes(R.color.dark_gray);

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.rename_flashcard_set)
                .icon(editIcon).iconPaddingDp(5)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.delete_flashcard_set)
                .icon(deleteIcon).iconPaddingDp(5)
                .backgroundColor(Color.WHITE)
                .build());

        new MaterialDialog.Builder(this)
                .title(title)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dialog.dismiss();
                        MaterialSimpleListItem item = adapter.getItem(which);
                        if (item.getContent().equals(getString(R.string.rename_flashcard_set))) {
                            showRenameDialog(position);
                        } else {
                            showDeleteDialog(position);
                        }
                    }
                })
                .show();
        return true;
    }

    public void showRenameDialog(final int listPosition) {
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
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        jankyCloseKeyboard();
                        if (which == DialogAction.POSITIVE) {
                            String newSetName = dialog.getInputEditText().getText().toString();
                            adapter.renameSet(listPosition, newSetName);
                        }
                    }
                })
                .show();
    }

    private void jankyCloseKeyboard() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.closeKeyboard(activity);
            }
        }, 200);
    }

    private void showDeleteDialog(final int listPosition) {
        new MaterialDialog.Builder(this)
                .title(R.string.flashcard_set_delete_title)
                .content(getString(R.string.flashcard_set_delete_message)
                        + "\"" + adapter.getItem(listPosition) + "\"?")
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adapter.deleteSet(listPosition);
                    }
                })
                .show();
    }

    @OnClick(R.id.add_set)
    public void addSet(View view) {
        String newSet = setName.getText().toString().trim();
        setName.setText("");
        if (newSet.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_name), Snackbar.LENGTH_LONG);
        }
        else if (DatabaseManager.get().doesSetExist(newSet)) {
            Utils.showSnackbar(parent, getString(R.string.set_already_exists), Snackbar.LENGTH_LONG);
        }
        else {
            adapter.addSet(newSet);
            Intent intent = new Intent(this, EditFlashcardSetActivity.class);
            intent.putExtra(FLASHCARD_SET_KEY, newSet);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
