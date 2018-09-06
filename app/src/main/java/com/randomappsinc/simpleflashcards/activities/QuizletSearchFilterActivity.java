package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.api.QuizletSearchManager;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizletSearchFilterActivity extends AppCompatActivity {

    @BindView(R.id.only_show_image_sets) CheckBox onlyShowImageSets;

    private QuizletSearchManager quizletSearchManager = QuizletSearchManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizlet_search_filter);
        ButterKnife.bind(this);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                .colorRes(R.color.white)
                .actionBarSize());

        onlyShowImageSets.setChecked(quizletSearchManager.getOnlyShowImageSets());
        onlyShowImageSets.jumpDrawablesToCurrentState();
    }

    @OnClick(R.id.apply_filter)
    public void applyFilter() {
        quizletSearchManager.setOnlyShowImageSets(onlyShowImageSets.isChecked());
        UIUtils.showShortToast(R.string.filter_applied, this);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
