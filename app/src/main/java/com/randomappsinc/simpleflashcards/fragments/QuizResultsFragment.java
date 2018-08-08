package com.randomappsinc.simpleflashcards.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.activities.PictureFullViewActivity;
import com.randomappsinc.simpleflashcards.adapters.QuizResultsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.models.Problem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class QuizResultsFragment extends Fragment {

    public static QuizResultsFragment getInstance(ArrayList<Problem> problems, int numQuestions) {
        QuizResultsFragment quizResultsFragment = new QuizResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.PROBLEMS_KEY, problems);
        bundle.putInt(Constants.NUM_QUESTIONS_KEY, numQuestions);
        quizResultsFragment.setArguments(bundle);
        return quizResultsFragment;
    }

    @BindView(R.id.quiz_results) RecyclerView quizResults;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.quiz_results_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);

        List<Problem> problems = getArguments().getParcelableArrayList(Constants.PROBLEMS_KEY);
        int numQuestions = getArguments().getInt(Constants.NUM_QUESTIONS_KEY);
        QuizResultsAdapter quizResultsAdapter = new QuizResultsAdapter(problems, numQuestions,resultClickListener);
        quizResults.setAdapter(quizResultsAdapter);
        return rootView;
    }

    private final QuizResultsAdapter.Listener resultClickListener = new QuizResultsAdapter.Listener() {
        @Override
        public void onImageClicked(String imageUrl) {
            openFullImageView(imageUrl);
        }
    };

    protected void openFullImageView(String imageUrl) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, PictureFullViewActivity.class)
                .putExtra(Constants.IMAGE_URL_KEY, imageUrl);
        startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
