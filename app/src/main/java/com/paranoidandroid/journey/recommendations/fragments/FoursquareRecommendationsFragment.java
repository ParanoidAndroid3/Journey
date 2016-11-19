package com.paranoidandroid.journey.recommendations.fragments;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.network.FoursquareVenueSearchClient;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FoursquareRecommendationsFragment extends BaseRecommendationsFragment implements
        RecommendationsListAdapterClickListener {

    public static FoursquareRecommendationsFragment newInstance(LatLng coordinates, String keyword, String typeTitle, long dayTime) {
        Bundle args = new Bundle();
        FoursquareRecommendationsFragment fragment = new FoursquareRecommendationsFragment();
        args.putParcelable("coordinates", coordinates);
        args.putString("keyword", keyword);
        args.putString("typeTitle", typeTitle);
        args.putLong("day_time", dayTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void search() {
        search(0, true);
    }

    private void search(int offset, final boolean clearExisting) {
        FoursquareVenueSearchClient.search(coordinates.latitude, coordinates.longitude, keyword, offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<FoursquareVenue> places = FoursquareVenue.parseJSON(response);
                appendItems(places, clearExisting);
            }
        });
    }

    @Override
    protected void decorateId(ParseObject activity, String id) {
        activity.put("foursquare_id", id);
    }

    public EndlessRecyclerViewScrollListener getEndlessScrollListener() {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("Foursquare", "onLoadMore:page:"+page+" totalItemsCount:"+totalItemsCount);
                search(totalItemsCount, false);
            }
        };
    }
}
