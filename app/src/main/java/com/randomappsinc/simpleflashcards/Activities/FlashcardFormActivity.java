package com.randomappsinc.simpleflashcards.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.Utils.Utils;
import com.rey.material.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class FlashcardFormActivity extends StandardActivity {
    public static final String UPDATE_MODE_KEY = "updateMode";
    public static final String QUESTION_KEY = "question";
    public static final String ANSWER_KEY = "answer";

    @Bind(R.id.parent) View parent;
    @Bind(R.id.question) EditText question;
    @Bind(R.id.answer) EditText answer;
    @Bind(R.id.flashcard_submit) Button submit;

    private String setName;
    private String currentQuestion;
    private String currentAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        boolean updateMode = getIntent().getBooleanExtra(UPDATE_MODE_KEY, false);
        if (updateMode) {
            submit.setText(R.string.update_flashcard);
            currentQuestion = getIntent().getStringExtra(QUESTION_KEY);
            currentAnswer = getIntent().getStringExtra(ANSWER_KEY);
        }
        else {
            submit.setText(R.string.add_flashcard);
        }

        setName = getIntent().getStringExtra(EditFlashcardSetActivity.FLASHCARD_SET_KEY);
    }

    @OnClick(R.id.flashcard_submit)
    public void submitFlashcard(View view) {
        String newQuestion = question.getText().toString().trim();
        String newAnswer = answer.getText().toString().trim();
        if (newQuestion.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_question));
        }
        else if (newAnswer.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_answer));
        }
    }
}
