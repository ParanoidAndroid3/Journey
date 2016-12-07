package com.paranoidandroid.journey.journeys.fragments;

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
    public void onItemSelected(Journey journey) {
     // todo: add some functionality if we want other peoples' journeys to be clickable.
    }

}
