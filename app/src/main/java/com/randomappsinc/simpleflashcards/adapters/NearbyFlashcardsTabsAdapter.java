package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.simpleflashcards.fragments.ReceivedFlashcardsFragment;
import com.randomappsinc.simpleflashcards.fragments.SendFlashcardsFragment;

public class NearbyFlashcardsTabsAdapter extends FragmentPagerAdapter {

    private String[] tabNames;

    public NearbyFlashcardsTabsAdapter(FragmentManager fragmentManager, String[] tabNames) {
        super(fragmentManager);
        this.tabNames = tabNames;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SendFlashcardsFragment();
            case 1:
                return new ReceivedFlashcardsFragment();
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
