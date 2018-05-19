package com.randomappsinc.simpleflashcards.activities;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.dialogs.QuitQuizDialog;
import com.randomappsinc.simpleflashcards.models.Quiz;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.Constants;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends StandardActivity implements QuitQuizDialog.Listener {

    @BindView(R.id.quiz_problem_container) View problemContainer;
    @BindView(R.id.question_header) TextView questionHeader;
    @BindView(R.id.question) TextView questionText;
    @BindView(R.id.options) RadioGroup optionsContainer;
    @BindViews({R.id.option_1, R.id.option_2, R.id.option_3, R.id.option_4}) List<RadioButton> optionButtons;
    @BindView(R.id.submit) View submitButton;

    @BindString(R.string.quiz_question_header) String headerTemplate;
    @BindInt(R.integer.shorter_anim_length) int animationLength;

    private Quiz quiz;
    private QuitQuizDialog quitQuizDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        FlashcardSet flashcardSet = DatabaseManager.get().getFlashcardSet(setId);
        setTitle(flashcardSet.getName());

        quitQuizDialog = new QuitQuizDialog(this, this);

        quiz = new Quiz(flashcardSet);
        int numOptions = quiz.getNumOptions();
        if (numOptions >= 3) {
            optionButtons.get(2).setVisibility(View.VISIBLE);
        }
        if (numOptions >= 4) {
            optionButtons.get(3).setVisibility(View.VISIBLE);
        }
        loadCurrentQuestionIntoView();
    }

    private void loadCurrentQuestionIntoView() {
        // Uncheck currently chosen option if applicable
        RadioButton chosenButton = getChosenButton();
        if (chosenButton != null) {
            optionsContainer.clearCheck();
            optionsContainer.jumpDrawablesToCurrentState();
        }
        String headerText = String.format(
                headerTemplate,
                quiz.getCurrentProblemPosition() + 1,
                quiz.getNumQuestions());
        questionHeader.setText(headerText);
        Quiz.Problem problem = quiz.getCurrentProblem();
        questionText.setText(problem.getQuestion());
        List<String> options = problem.getOptions();
        for (int i = 0; i < options.size(); i++) {
            optionButtons.get(i).setText(options.get(i));
        }
    }

    private void animateQuestionOut() {
        submitButton.setEnabled(false);
        problemContainer
                .animate()
                .translationXBy(-1 * problemContainer.getWidth())
                .alpha(0)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        problemContainer.setTranslationX(0);
                        loadCurrentQuestionIntoView();
                        animationQuestionIn();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeQuestionViewSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    private void animationQuestionIn() {
        problemContainer
                .animate()
                .alpha(1)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        submitButton.setEnabled(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeQuestionViewSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    private void makeQuestionViewSane() {
        problemContainer.setTranslationX(0);
        problemContainer.setAlpha(1);
        loadCurrentQuestionIntoView();
        submitButton.setEnabled(true);
    }

    @OnClick(R.id.submit)
    public void submitAnswer() {
        RadioButton chosenButton = getChosenButton();
        if (chosenButton == null) {
            UIUtils.showLongToast(R.string.please_check_something);
        } else {
            quiz.submitAnswer(chosenButton.getText().toString());
            quiz.advanceToNextProblem();
            if (quiz.isQuizComplete()) {
                // TODO: Show results page
                submitButton.setVisibility(View.GONE);
            } else {
                animateQuestionOut();
            }
        }
    }

    @Nullable
    private RadioButton getChosenButton() {
        for (RadioButton radioButton : optionButtons) {
            if (radioButton.isChecked()) {
                return radioButton;
            }
        }
        return null;
    }

    private void onQuizExit() {
        if (!quiz.isQuizComplete()) {
            quitQuizDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onQuitQuizConfirmed() {
        finish();
    }

    @Override
    public void onBackPressed() {
        onQuizExit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onQuizExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
