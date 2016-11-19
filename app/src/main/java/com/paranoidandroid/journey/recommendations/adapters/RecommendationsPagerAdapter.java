package com.paranoidandroid.journey.recommendations.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;
import com.paranoidandroid.journey.recommendations.fragments.FoursquareRecommendationsFragment;
import com.paranoidandroid.journey.support.ui.SmartFragmentStatePagerAdapter;
import com.paranoidandroid.journey.recommendations.fragments.GoogleRecommendationsFragment;

import java.util.List;

public class RecommendationsPagerAdapter extends SmartFragmentStatePagerAdapter {

    List<RecommendationsActivity.Keyword> tabs;
    LatLng near;
    long dayTime;

    public static RecommendationsPagerAdapter newInstance(
            FragmentManager fragmentManager,
            List<RecommendationsActivity.Keyword> tabs,
            LatLng near,
            long dayTime) {
        RecommendationsPagerAdapter adapter = new RecommendationsPagerAdapter(fragmentManager);
        adapter.tabs = tabs;
        adapter.near = near;
        adapter.dayTime = dayTime;
        return adapter;
    }

    public RecommendationsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (tabs.get(position).type) {
            default:
            case 0: // Google
                return GoogleRecommendationsFragment.newInstance(near, tabs.get(position).keyword, tabs.get(position).tabTitle, dayTime);
            case 1: // Foursquare
                return FoursquareRecommendationsFragment.newInstance(near, tabs.get(position).keyword, tabs.get(position).tabTitle, dayTime);
        }
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).tabTitle;
    }
}
