package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.utils.MiscUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends StandardActivity {

    public static final String FLASHCARD_SET_KEY = "flashcardSet";

    @BindView(R.id.set_name) EditText setName;
    @BindView(R.id.add_icon) ImageView addButton;
    @BindView(R.id.flashcard_sets) ListView sets;
    @BindView(R.id.no_sets) TextView noSets;
    @BindView(R.id.parent) View parent;

    private FlashcardSetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshContent();
    }

    @OnItemClick(R.id.flashcard_sets)
    public void onFlashcardSetClick(int position) {
        Intent intent = new Intent(this, StudyModeActivity.class);
        intent.putExtra(FLASHCARD_SET_KEY, adapter.getItem(position));
        startActivity(intent);
    }

    @OnClick(R.id.add_set)
    public void addSet() {
        String newSet = setName.getText().toString().trim();
        setName.setText("");
        if (newSet.isEmpty()) {
            MiscUtils.showSnackbar(parent, getString(R.string.blank_name), Snackbar.LENGTH_LONG);
        } else if (DatabaseManager.get().doesSetExist(newSet)) {
            MiscUtils.showSnackbar(parent, getString(R.string.set_already_exists), Snackbar.LENGTH_LONG);
        } else {
            DatabaseManager.get().addFlashcardSet(newSet);
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
