package com.randomappsinc.simpleflashcards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.StringUtils;

public class NearbyFlashcardsTabsAdapter extends FragmentPagerAdapter {

    private String[] favoriteTabs;

    public NearbyFlashcardsTabsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        favoriteTabs = StringUtils.getStringArray(R.array.nearby_flashcards_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Fragment();
            case 1:
                return new Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return favoriteTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return favoriteTabs[position];
    }
}
