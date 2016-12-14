package com.paranoidandroid.journey.journeys.fragments;

import android.view.View;

import com.paranoidandroid.journey.models.Journey;
import com.parse.ParseQuery;

/**
 * Load and display a list of all journeys.
 */
public class AllJourneysListFragment extends JourneysListFragment {

    public static AllJourneysListFragment newInstance() {
        return new AllJourneysListFragment();
    }

    @Override
    protected ParseQuery<Journey> generateQueryForJourneys() {
        return Journey.createQuery();
    }

    @Override
    protected void showEmptyView(boolean isEmpty) {
        binding.rlEmptyView.setVisibility(View.GONE);
        if (isEmpty) {
            binding.rlIcon.setVisibility(View.VISIBLE);
            binding.rvJourneys.setVisibility(View.GONE);
        } else {
            binding.rlIcon.setVisibility(View.GONE);
            binding.rvJourneys.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(Journey journey) {
        // nothing right now
    }

}
