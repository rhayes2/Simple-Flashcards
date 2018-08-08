package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.QuizResultsTabsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.models.Problem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizResultsActivity extends StandardActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.quiz_results_tabs) TabLayout tabs;
    @BindView(R.id.quiz_results_pager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_results);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                .colorRes(R.color.white)
                .actionBarSize());

        List<Problem> problems = getIntent().getParcelableArrayListExtra(Constants.QUIZ_RESULTS_KEY);
        viewPager.setAdapter(new QuizResultsTabsAdapter(getSupportFragmentManager(), problems));
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}
