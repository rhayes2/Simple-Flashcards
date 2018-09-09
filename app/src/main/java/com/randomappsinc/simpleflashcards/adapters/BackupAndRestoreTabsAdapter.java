package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.simpleflashcards.fragments.BackupDataFragment;
import com.randomappsinc.simpleflashcards.fragments.RestoreDataFragment;

public class BackupAndRestoreTabsAdapter extends FragmentPagerAdapter {

    private String[] tabNames;

    public BackupAndRestoreTabsAdapter(FragmentManager fragmentManager, String[] tabNames) {
        super(fragmentManager);
        this.tabNames = tabNames;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BackupDataFragment();
            case 1:
                return new RestoreDataFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }
}
