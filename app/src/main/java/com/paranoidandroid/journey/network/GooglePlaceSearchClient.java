package com.paranoidandroid.journey.network;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class GooglePlaceSearchClient {
    private static final String NEARBY_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void search(double latitude, double longitude, String type, String pageToken, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("location", latitude + "," + longitude);
        params.put("radius", 5000);
        params.put("type", type);
        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }
        params.put("key", "AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y");
        System.out.println(params);
        client.get(NEARBY_SEARCH_BASE_URL, params, responseHandler);
    }
}