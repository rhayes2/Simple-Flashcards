package com.randomappsinc.simpleflashcards.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.simpleflashcards.Persistence.DataObjects.Flashcard;
import com.randomappsinc.simpleflashcards.Persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.Utils.MiscUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 11/24/15.
 */
public class StudyModeActivity extends StandardActivity {
    @Bind(R.id.set_name) TextView flashcardSetTitle;
    @Bind(R.id.no_flashcards) View noFlashcards;
    @Bind(R.id.question_answer_pair) View qaPair;
    @Bind(R.id.question) TextView question;
    @Bind(R.id.show_answer) View showAnswer;
    @Bind(R.id.answer) TextView answer;

    private List<Flashcard> flashcards;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        String setName = getIntent().getStringExtra(MainActivity.FLASHCARD_SET_KEY);
        flashcardSetTitle.setText(setName);
        flashcards = DatabaseManager.get().getAllFlashcards(setName);
        if (flashcards.size() == 0) {
            qaPair.setVisibility(View.GONE);
            noFlashcards.setVisibility(View.VISIBLE);
        }
        else {
            noFlashcards.setVisibility(View.GONE);
            qaPair.setVisibility(View.VISIBLE);
            currentPosition = 0;
            setUpFlashcard();
        }
    }

    public void setUpFlashcard() {
        Flashcard flashcard = flashcards.get(currentPosition);
        question.setText(flashcard.getQuestion());
        answer.setVisibility(View.GONE);
        showAnswer.setVisibility(View.VISIBLE);
        answer.setText(flashcard.getAnswer());
    }

    @OnClick(R.id.show_answer)
    public void showAnswer() {
        showAnswer.setVisibility(View.GONE);
        answer.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (flashcards.size() != 0) {
            getMenuInflater().inflate(R.menu.study_mode_menu, menu);
            menu.findItem(R.id.random_flashcard).setIcon(
                    new IconDrawable(this, FontAwesomeIcons.fa_random)
                            .colorRes(R.color.white)
                            .actionBarSize());
            menu.findItem(R.id.previous_flashcard).setIcon(
                    new IconDrawable(this, FontAwesomeIcons.fa_arrow_left)
                            .colorRes(R.color.white)
                            .actionBarSize());
            menu.findItem(R.id.next_flashcard).setIcon(
                    new IconDrawable(this, FontAwesomeIcons.fa_arrow_right)
                            .colorRes(R.color.white)
                            .actionBarSize());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.random_flashcard:
                if (flashcards.size() != 1) {
                    currentPosition = MiscUtils.getRandomFlashcardPosition(flashcards.size(), currentPosition);
                }
                setUpFlashcard();
                return true;
            case R.id.previous_flashcard:
                if (currentPosition == 0) {
                    currentPosition = flashcards.size() - 1;
                }
                else {
                    currentPosition--;
                }
                setUpFlashcard();
                return true;
            case R.id.next_flashcard:
                if (currentPosition == flashcards.size() - 1) {
                    currentPosition = 0;
                }
                else {
                    currentPosition++;
                }
                setUpFlashcard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
