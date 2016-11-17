package com.paranoidandroid.journey.recommendations.fragments;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.network.FoursquareVenueSearchClient;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FoursquareRecommendationsFragment extends BaseRecommendationsFragment implements
        RecommendationsListAdapterClickListener {

    public static FoursquareRecommendationsFragment newInstance(LatLng coordinates, String keyword) {
        Bundle args = new Bundle();
        FoursquareRecommendationsFragment fragment = new FoursquareRecommendationsFragment();
        args.putParcelable("coordinates", coordinates);
        args.putString("keyword", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    public void search() {
        FoursquareVenueSearchClient.search(coordinates.latitude, coordinates.longitude, keyword, 0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<FoursquareVenue> places = FoursquareVenue.parseJSON(response);
                appendItems(places);
            }
        });
    }
}
