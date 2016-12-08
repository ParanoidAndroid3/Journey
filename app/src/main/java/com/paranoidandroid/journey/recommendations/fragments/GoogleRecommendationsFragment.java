package com.paranoidandroid.journey.recommendations.fragments;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GoogleRecommendationsFragment extends BaseRecommendationsFragment implements
        RecommendationsListAdapterClickListener {

    // Implements search and endless scrolling for Google Places

    private String nextToken = null;
    private boolean hasMore = true;

    public static GoogleRecommendationsFragment newInstance(LatLng coordinates, RecommendationCategory category) {
        Bundle args = new Bundle();
        GoogleRecommendationsFragment fragment = new GoogleRecommendationsFragment();
        args.putParcelable("coordinates", coordinates);
        args.putParcelable("category", Parcels.wrap(category));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void search() {
        search(null, true);
    }

    private void search(String token, final boolean clearExisting) {
        GooglePlaceSearchClient.search(coordinates.latitude, coordinates.longitude, category.id, token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<GooglePlace> places = GooglePlace.parseJSON(response);
                nextToken = GooglePlace.parseNextToken(response);
                hasMore = nextToken != null;
                appendItems(places, clearExisting);
            }
        });
    }

    public EndlessRecyclerViewScrollListener getScrollListener() {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (hasMore)
                    search(nextToken, false);
            }
        };
    }
}
