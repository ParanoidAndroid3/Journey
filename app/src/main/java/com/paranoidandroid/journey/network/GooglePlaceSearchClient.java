package com.paranoidandroid.journey.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GooglePlaceSearchClient {
    private static final String NEARBY_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String PLACE_DETAILS_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json";
    private static final String AUTO_COMPLETE_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyATUw7QPfDafx9TJ2NszA7TdUmkqfisfgA";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void search(double latitude, double longitude, String type, String pageToken, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        if (pageToken !=  null) {
            params.put("pagetoken", pageToken);
        } else {
            params.put("location", latitude + "," + longitude);
            params.put("radius", 5000);
            params.put("type", type);
            params.put("language", "en");
        }
        params.put("key", GOOGLE_PLACES_API_KEY);
        System.out.println(params);
        client.get(NEARBY_SEARCH_BASE_URL, params, responseHandler);
    }

    public static void autoComplete(String input, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("key", GOOGLE_PLACES_API_KEY);
        params.put("types", "(cities)");
        params.put("input", input);

        client.get(AUTO_COMPLETE_BASE_URL, params, responseHandler);
    }

    public static void findDetails(String id, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("placeid", id);
        params.put("key", GOOGLE_PLACES_API_KEY);
        System.out.println(params);
        client.get(PLACE_DETAILS_BASE_URL, params, responseHandler);
    }

    public static void getPlaceDetails(String placeId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("key", GOOGLE_PLACES_API_KEY);
        params.put("placeid", placeId);
        client.get(PLACE_DETAILS_BASE_URL, params, responseHandler);
    }

}
