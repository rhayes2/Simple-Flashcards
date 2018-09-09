package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.BackupAndRestoreTabsAdapter;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BackupAndRestoreActivity extends StandardActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.backup_and_restore_tabs) TabLayout tabs;
    @BindView(R.id.backup_and_restore_pager) ViewPager viewPager;
    @BindArray(R.array.backup_and_restore_tabs) String[] tabNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_and_restore);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setAdapter(new BackupAndRestoreTabsAdapter(getSupportFragmentManager(), tabNames));
        tabs.setupWithViewPager(viewPager);
    }
}