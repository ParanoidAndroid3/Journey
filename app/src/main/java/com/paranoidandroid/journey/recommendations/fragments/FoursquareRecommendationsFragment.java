package com.paranoidandroid.journey.recommendations.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.network.FoursquareVenueSearchClient;
import com.paranoidandroid.journey.recommendations.activities.RecommendationDetailActivity;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FoursquareRecommendationsFragment extends BaseRecommendationsFragment implements
        RecommendationsListAdapterClickListener {

    // Implements search and endless scrolling for Foursquare

    public static FoursquareRecommendationsFragment newInstance(LatLng coordinates, RecommendationCategory category) {
        Bundle args = new Bundle();
        FoursquareRecommendationsFragment fragment = new FoursquareRecommendationsFragment();
        args.putParcelable("coordinates", coordinates);
        args.putParcelable("category", Parcels.wrap(category));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void search() {
        search(0, true);
    }

    private void search(int offset, final boolean clearExisting) {
        FoursquareVenueSearchClient.search(coordinates.latitude, coordinates.longitude, category.id, offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<FoursquareVenue> places = FoursquareVenue.parseJSON(response);
                appendItems(places, clearExisting);
            }
        });
    }

    public EndlessRecyclerViewScrollListener getScrollListener() {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                search(totalItemsCount, false);
            }
        };
    }
}
