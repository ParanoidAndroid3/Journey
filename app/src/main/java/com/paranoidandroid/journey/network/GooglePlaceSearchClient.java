package com.paranoidandroid.journey.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GooglePlaceSearchClient {
    private static final String NEARBY_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String AUTO_COMPLETE_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void search(double latitude, double longitude, String type, String pageToken, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("location", latitude + "," + longitude);
        params.put("radius", 5000);
        params.put("type", type);
        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }
        params.put("key", GOOGLE_PLACES_API_KEY);
        System.out.println(params);
        client.get(NEARBY_SEARCH_BASE_URL, params, responseHandler);
    }

    public static void autoComplete(String input, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("key", GOOGLE_PLACES_API_KEY);
        params.put("types", "geocode");
        params.put("input", input);

        client.get(GOOGLE_PLACES_API_KEY, params, responseHandler);
    }
}