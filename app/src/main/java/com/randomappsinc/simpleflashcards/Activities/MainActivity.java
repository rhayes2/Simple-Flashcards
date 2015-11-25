package com.randomappsinc.simpleflashcards.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.Adapters.FlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.Persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.Utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends StandardActivity {
    public static final String FLASHCARD_SET_KEY = "flashcardSet";

    @Bind(R.id.set_name) EditText setName;
    @Bind(R.id.add_icon) ImageView addButton;
    @Bind(R.id.flashcard_sets) ListView sets;
    @Bind(R.id.no_sets) TextView noSets;
    @Bind(R.id.parent) View parent;

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

    @OnItemClick(R.id.flashcard_sets)
    public void onFlashcardSetClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, StudyModeActivity.class);
        intent.putExtra(FLASHCARD_SET_KEY, adapter.getItem(position));
        startActivity(intent);
    }

    @OnClick(R.id.add_set)
    public void addSet(View view) {
        String newSet = setName.getText().toString().trim();
        setName.setText("");
        if (newSet.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_name));
        }
        else if (DatabaseManager.get().doesSetExist(newSet)) {
            Utils.showSnackbar(parent, getString(R.string.set_already_exists));
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
