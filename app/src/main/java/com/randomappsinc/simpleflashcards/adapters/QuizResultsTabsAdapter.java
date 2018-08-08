package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.fragments.QuizResultsFragment;
import com.randomappsinc.simpleflashcards.models.Problem;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class QuizResultsTabsAdapter extends FragmentPagerAdapter {

    private String[] resultsTabs;
    private List<Problem> problems;

    public QuizResultsTabsAdapter(FragmentManager fragmentManager, List<Problem> problems) {
        super(fragmentManager);
        this.resultsTabs = StringUtils.getStringArray(R.array.quiz_results_tabs);
        this.problems = problems;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ArrayList<Problem> wrongProblems = new ArrayList<>();
                for (Problem problem : problems) {
                    if (!problem.wasUserCorrect()) {
                        wrongProblems.add(problem);
                    }
                }
                return QuizResultsFragment.getInstance(wrongProblems, problems.size(), true);
            case 1:
                ArrayList<Problem> rightProblems = new ArrayList<>();
                for (Problem problem : problems) {
                    if (problem.wasUserCorrect()) {
                        rightProblems.add(problem);
                    }
                }
                return QuizResultsFragment.getInstance(rightProblems, problems.size(), false);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return resultsTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return resultsTabs[position];
    }
}
