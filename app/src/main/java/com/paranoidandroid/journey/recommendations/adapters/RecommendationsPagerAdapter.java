package com.paranoidandroid.journey.recommendations.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.recommendations.fragments.FoursquareRecommendationsFragment;
import com.paranoidandroid.journey.recommendations.fragments.GoogleRecommendationsFragment;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.paranoidandroid.journey.support.ui.SmartFragmentStatePagerAdapter;

import java.util.List;

public class RecommendationsPagerAdapter extends SmartFragmentStatePagerAdapter {

    List<RecommendationCategory> categories;
    LatLng near;

    public static RecommendationsPagerAdapter newInstance(
            FragmentManager fragmentManager,
            List<RecommendationCategory> categories,
            LatLng near) {
        RecommendationsPagerAdapter adapter = new RecommendationsPagerAdapter(fragmentManager);
        adapter.categories = categories;
        adapter.near = near;
        return adapter;
    }

    public RecommendationsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public View getTabView(int i) {
        return null;
    }

    // Create a fragment according to the API source id

    @Override
    public Fragment getItem(int position) {
        RecommendationCategory category = categories.get(position);

        switch (category.source) {
            case GOOGLE:
                return GoogleRecommendationsFragment.newInstance(near, category);
            case FOURSQUARE:
                return FoursquareRecommendationsFragment.newInstance(near, category);
        }

        return new Fragment();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position).title;
    }
}
