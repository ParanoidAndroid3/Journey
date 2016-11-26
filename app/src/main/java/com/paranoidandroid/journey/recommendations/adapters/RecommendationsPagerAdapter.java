package com.paranoidandroid.journey.recommendations.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;
import com.paranoidandroid.journey.recommendations.fragments.FoursquareRecommendationsFragment;
import com.paranoidandroid.journey.support.ui.SmartFragmentStatePagerAdapter;
import com.paranoidandroid.journey.recommendations.fragments.GoogleRecommendationsFragment;

import java.util.List;

public class RecommendationsPagerAdapter extends SmartFragmentStatePagerAdapter {

    List<RecommendationsActivity.Keyword> keywordList;
    LatLng near;

    public static RecommendationsPagerAdapter newInstance(
            FragmentManager fragmentManager,
            List<RecommendationsActivity.Keyword> keywords,
            LatLng near) {
        RecommendationsPagerAdapter adapter = new RecommendationsPagerAdapter(fragmentManager);
        adapter.keywordList = keywords;
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
        if (keywordList.get(position).sourceId.equals("google_id")) {
            return GoogleRecommendationsFragment.newInstance(near, keywordList.get(position));
        } else if (keywordList.get(position).sourceId.equals("foursquare_id")) {
            return FoursquareRecommendationsFragment.newInstance(near, keywordList.get(position));
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return keywordList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return keywordList.get(position).title;
    }
}
