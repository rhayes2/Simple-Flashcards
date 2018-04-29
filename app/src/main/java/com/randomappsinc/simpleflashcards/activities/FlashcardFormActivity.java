package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
import com.rey.material.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlashcardFormActivity extends StandardActivity {

    public static final String UPDATE_MODE_KEY = "updateMode";
    public static final String QUESTION_KEY = "question";
    public static final String ANSWER_KEY = "answer";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.question) EditText question;
    @BindView(R.id.answer) EditText answer;
    @BindView(R.id.flashcard_submit) Button submit;

    private boolean updateMode;
    private String setName;
    private String currentQuestion;
    private String currentAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        updateMode = getIntent().getBooleanExtra(UPDATE_MODE_KEY, false);
        if (updateMode) {
            submit.setText(R.string.update_flashcard);
            currentQuestion = getIntent().getStringExtra(QUESTION_KEY);
            question.setText(currentQuestion);
            currentAnswer = getIntent().getStringExtra(ANSWER_KEY);
            answer.setText(currentAnswer);
        }
        else {
            submit.setText(R.string.add_flashcard);
        }
        setName = getIntent().getStringExtra(MainActivity.FLASHCARD_SET_KEY);
    }

    @OnClick(R.id.flashcard_submit)
    public void submitFlashcard() {
        UIUtils.closeKeyboard(this);
        String newQuestion = question.getText().toString().trim();
        String newAnswer = answer.getText().toString().trim();
        if (newQuestion.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_question), Snackbar.LENGTH_LONG);
        }
        else if (newAnswer.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_answer), Snackbar.LENGTH_LONG);
        }
        else {
            if (DatabaseManager.get().doesFlashcardExist(setName, newQuestion, newAnswer)) {
                UIUtils.showSnackbar(parent, getString(R.string.dupe_flashcard), Snackbar.LENGTH_LONG);
            }
            else {
                if (updateMode) {
                    DatabaseManager.get().updateFlashcard(currentQuestion, currentAnswer,
                            newQuestion, newAnswer, setName);
                    finish();
                }
                else {
                    DatabaseManager.get().addFlashcard(newQuestion, newAnswer, setName);
                    UIUtils.showSnackbar(parent, getString(R.string.flashcard_added), Snackbar.LENGTH_SHORT);
                    question.setText("");
                    answer.setText("");
                    question.requestFocus();
                }
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
                            DatabaseManager.get().deleteFlashcard(currentQuestion, currentAnswer, setName);
                            finish();
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}