package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlashcardFormActivity extends StandardActivity {

    public static final String UPDATE_MODE_KEY = "updateMode";
    public static final String FLASHCARD_ID_KEY = "flashcardId";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.term) EditText question;
    @BindView(R.id.definition) EditText answer;
    @BindView(R.id.flashcard_submit) TextView submit;

    private boolean updateMode;
    private int setId;
    private int flashcardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        updateMode = getIntent().getBooleanExtra(UPDATE_MODE_KEY, false);
        if (updateMode) {
            submit.setText(R.string.update_flashcard);
            flashcardId = getIntent().getIntExtra(FLASHCARD_ID_KEY, 0);
            Flashcard flashcard = DatabaseManager.get().getFlashcard(flashcardId);
            question.setText(flashcard.getTerm());
            answer.setText(flashcard.getDefinition());
        } else {
            submit.setText(R.string.add_flashcard);
        }
        setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
    }

    @OnClick(R.id.flashcard_submit)
    public void submitFlashcard() {
        UIUtils.closeKeyboard(this);
        String newQuestion = question.getText().toString().trim();
        String newAnswer = answer.getText().toString().trim();
        if (newQuestion.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_term), Snackbar.LENGTH_LONG);
        } else if (newAnswer.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_definition), Snackbar.LENGTH_LONG);
        } else {
            if (updateMode) {
                DatabaseManager.get().updateFlashcard(flashcardId, newQuestion, newAnswer);
                finish();
            } else {
                DatabaseManager.get().addFlashcard(setId, newQuestion, newAnswer);
                UIUtils.showSnackbar(parent, getString(R.string.flashcard_added), Snackbar.LENGTH_SHORT);
                question.setText("");
                answer.setText("");
                question.requestFocus();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (updateMode) {
            getMenuInflater().inflate(R.menu.update_flashcard_menu, menu);
            menu.findItem(R.id.delete_flashcard).setIcon(
                    new IconDrawable(this, IoniconsIcons.ion_android_delete)
                            .colorRes(R.color.white)
                            .actionBarSize());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_flashcard) {
            new MaterialDialog.Builder(this)
                    .title(R.string.flashcard_delete_title)
                    .content(R.string.flashcard_delete_message)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            DatabaseManager.get().deleteFlashcard(flashcardId);
                            finish();
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
