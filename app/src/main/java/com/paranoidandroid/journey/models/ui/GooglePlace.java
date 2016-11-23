package com.paranoidandroid.journey.models.ui;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

@Parcel
public class GooglePlace extends Recommendation {
    private String photoReference;

    /**
     * Gets notified when a request for an image URL has completed.
     */
    public interface OnImageReferenceReadyHandler {
        void onImageReferenceReady(String imageReference);
        void onFailure(Throwable throwable);
    }

    public static String makeImageUrl(String imageReference) {
        return makeImageUrl(imageReference, 400);
    }

    public static String makeImageUrl(String imageReference, int maxWidth) {
        if (imageReference == null) {
            return null;
        }
        return String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photoreference=%s&key=%s",
                maxWidth, imageReference, "AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y");
    }

    /**
     * Downloads an image reference for a Google Place.
     *
     * The image reference can be turned into an image URL with #makeImageUrl().
     *
     * @param googlePlaceId ID for a Google Place.
     * @param responseHandler Notified when the image reference has been downloaded or on failure.
     */
    public static void getPlaceImageAsync(String googlePlaceId, OnImageReferenceReadyHandler responseHandler) {
        final OnImageReferenceReadyHandler handler = responseHandler;
        GooglePlaceSearchClient.findDetails(googlePlaceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                GooglePlace place = parsePlaceDetails(response);
                if (place != null && place.photoReference != null) {
                    handler.onImageReferenceReady(place.photoReference);
                } else {
                    handler.onFailure(new Exception("No photo found"));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONObject errorResponse) {
                handler.onFailure(throwable);
            }
        });
    }

    public static GooglePlace parsePlace(JSONObject jsonPlace) throws JSONException {
        GooglePlace gp = new GooglePlace();
        JSONObject location = jsonPlace.getJSONObject("geometry").getJSONObject("location");
        gp.id = jsonPlace.getString("id");
        gp.name = jsonPlace.getString("name");
        gp.lat = location.getDouble("lat");
        gp.lng = location.getDouble("lng");
        if (!jsonPlace.isNull("vicinity"))
            gp.address = jsonPlace.getString("vicinity");
        gp.rating = (!jsonPlace.isNull("rating")) ? jsonPlace.getDouble("rating") : -1.;
        if (!jsonPlace.isNull("photos")) {
            gp.photoReference = jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            gp.imageUrl = makeImageUrl(gp.photoReference);
        }
        return gp;
    }

    public static GooglePlace parsePlaceDetails(JSONObject jsonPlace) {
        try {
            JSONObject result = jsonPlace.getJSONObject("result");
             return parsePlace(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<GooglePlace> parseJSON(JSONObject response) {
        List<GooglePlace> list = new ArrayList<>();
        try {
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                list.add(parsePlace(results.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String parseNextToken(JSONObject response) {
        String nextToken = null;
        try {
            if (!response.isNull("next_page_token"))
                nextToken = response.getString("next_page_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nextToken;
    }
}
