package com.randomappsinc.simpleflashcards.activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.constants.QuizScore;
import com.randomappsinc.simpleflashcards.dialogs.QuitQuizDialog;
import com.randomappsinc.simpleflashcards.models.Problem;
import com.randomappsinc.simpleflashcards.models.Quiz;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends StandardActivity implements QuitQuizDialog.Listener {

    @BindView(R.id.problem_parent) View problemParent;
    @BindView(R.id.question_header) TextView questionHeader;
    @BindView(R.id.question) TextView questionText;
    @BindView(R.id.question_image) ImageView questionImage;
    @BindView(R.id.options) RadioGroup optionsContainer;
    @BindViews({R.id.option_1, R.id.option_2, R.id.option_3, R.id.option_4}) List<RadioButton> optionButtons;
    @BindView(R.id.submit) View submitButton;
    @BindView(R.id.results_page) View resultsPage;
    @BindView(R.id.results_header) TextView resultsHeader;
    @BindView(R.id.score) TextView score;

    @BindString(R.string.quiz_question_header) String headerTemplate;
    @BindString(R.string.good_score_message) String goodScore;
    @BindString(R.string.okay_score_message) String okayScore;
    @BindString(R.string.bad_score_message) String badScore;
    @BindString(R.string.your_score_was) String scoreHeaderTemplate;
    @BindString(R.string.quiz_score_template) String scoreTemplate;
    @BindInt(R.integer.shorter_anim_length) int animationLength;

    private FlashcardSet flashcardSet;
    private Quiz quiz;
    private QuitQuizDialog quitQuizDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        flashcardSet = DatabaseManager.get().getFlashcardSet(setId);
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

    protected void loadCurrentQuestionIntoView() {
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
        Problem problem = quiz.getCurrentProblem();
        questionText.setText(problem.getQuestion());

        String imageUrl = problem.getQuestionImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            questionImage.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).into(questionImage);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        List<String> options = problem.getOptions();
        for (int i = 0; i < options.size(); i++) {
            optionButtons.get(i).setText(options.get(i));
        }
    }

    @OnClick(R.id.question_image)
    public void openImageInFullView() {
        String imageUrl = quiz.getCurrentProblem().getQuestionImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Intent intent = new Intent(this, PictureFullViewActivity.class)
                    .putExtra(Constants.IMAGE_URL_KEY, imageUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, 0);
        }
    }

    private void animateQuestionOut() {
        submitButton.setEnabled(false);
        problemParent
                .animate()
                .translationXBy(-1 * problemParent.getWidth())
                .alpha(0)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        problemParent.setTranslationX(0);
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

    protected void animationQuestionIn() {
        problemParent
                .animate()
                .alpha(1)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loadCurrentQuestionIntoView();
                    }

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

    protected void makeQuestionViewSane() {
        problemParent.setTranslationX(0);
        problemParent.setAlpha(1);
        loadCurrentQuestionIntoView();
        submitButton.setEnabled(true);
        resultsPage.setVisibility(View.GONE);
        resultsPage.setAlpha(0);
        problemParent.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    protected void makeResultsPageSane() {
        resultsPage.setAlpha(1);
        resultsPage.setVisibility(View.VISIBLE);
        problemParent.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.submit)
    public void submitAnswer() {
        if (quiz.isQuizComplete()) {
            return;
        }

        RadioButton chosenButton = getChosenButton();
        if (chosenButton == null) {
            UIUtils.showLongToast(R.string.please_check_something);
        } else {
            quiz.submitAnswer(chosenButton.getText().toString());
            quiz.advanceToNextProblem();
            if (quiz.isQuizComplete()) {
                fadeOutProblemPage();
            } else {
                animateQuestionOut();
            }
        }
    }

    private void fadeOutProblemPage() {
        problemParent
                .animate()
                .alpha(0)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        problemParent.setVisibility(View.GONE);
                        submitButton.setVisibility(View.GONE);
                        fadeInResultsPage();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeResultsPageSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
        submitButton.animate().alpha(0).setDuration(animationLength);
    }

    protected void loadResultsIntoView() {
        Quiz.Grade grade = quiz.getGrade();
        String quizScore = "";
        switch (grade.getScore()) {
            case QuizScore.GOOD:
                quizScore = goodScore;
                break;
            case QuizScore.OKAY:
                quizScore = okayScore;
                break;
            case QuizScore.BAD:
                quizScore = badScore;
                break;
        }
        String scoreHeaderText = String.format(
                Locale.getDefault(),
                scoreHeaderTemplate,
                quizScore);
        resultsHeader.setText(scoreHeaderText);
        String scoreText = String.format(
                Locale.getDefault(),
                scoreTemplate,
                grade.getFractionText(),
                grade.getPercentText());
        score.setText(scoreText);
    }

    protected void fadeInResultsPage() {
        resultsPage
                .animate()
                .alpha(1)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loadResultsIntoView();
                        resultsPage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {}

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeResultsPageSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    private void fadeOutResultsPage() {
        resultsPage
                .animate()
                .alpha(0)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resultsPage.setVisibility(View.GONE);
                        fadeInProblemPage();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeQuestionViewSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    protected void fadeInProblemPage() {
        problemParent
                .animate()
                .alpha(1)
                .setDuration(animationLength)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loadCurrentQuestionIntoView();
                        problemParent.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {}

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        makeQuestionViewSane();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
        submitButton.animate().alpha(1).setDuration(animationLength);
    }

    @OnClick(R.id.retake)
    public void retake() {
        quiz = new Quiz(flashcardSet);
        fadeOutResultsPage();
    }

    @OnClick(R.id.exit)
    public void exit() {
        finish();
    }

    @OnClick(R.id.view_results)
    public void viewResults() {
        Intent intent = new Intent(this, QuizResultsActivity.class)
                .putParcelableArrayListExtra(Constants.QUIZ_RESULTS_KEY, quiz.getProblems())
                .putExtra(Constants.FLASHCARD_SET_NAME_KEY, flashcardSet.getName())
                .putExtra(Constants.FLASHCARD_SET_SIZE_KEY, flashcardSet.getFlashcards().size());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
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
