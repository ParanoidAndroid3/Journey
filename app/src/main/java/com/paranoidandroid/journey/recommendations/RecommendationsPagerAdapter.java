package com.paranoidandroid.journey.recommendations;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class RecommendationsPagerAdapter extends SmartFragmentStatePagerAdapter {

    List<String> tabNames;

    public static RecommendationsPagerAdapter newInstance(FragmentManager fragmentManager, List<String> tabNames) {
        RecommendationsPagerAdapter adapter = new RecommendationsPagerAdapter(fragmentManager);
        adapter.tabNames = tabNames;
        return adapter;
    }

    public RecommendationsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return new Fragment();
    }

    @Override
    public int getCount() {
        return tabNames.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }
}
