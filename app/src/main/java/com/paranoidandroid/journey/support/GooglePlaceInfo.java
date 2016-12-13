package com.paranoidandroid.journey.support;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Caches information on a Google Place ID.
 */
public class GooglePlaceInfo {
    private static final String TAG = "GooglePlaceInfo";

    private final String photoReference;
    private final double latitude;
    private final double longitude;

    public GooglePlaceInfo(String photoReference, double latitude, double longitude) {
        this.photoReference = photoReference;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GooglePlaceInfo(String photoReference, LatLng coordinates) {
        this.photoReference = photoReference;
        this.latitude = (coordinates != null) ? coordinates.latitude : 0;
        this.longitude = (coordinates != null) ? coordinates.longitude : 0;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl(int maxWidth) {
        String url;
        if (photoReference == null) {
            // Pull image of map from google maps API.
            url = String.format(Locale.US,
                    "http://maps.google.com/maps/api/staticmap?center=%1$f,%2$f&zoom=12&size=%3$dx%4$d&markers=%1$f,%2$f",
                    latitude, longitude, maxWidth, maxWidth / 2);
        } else {
            url = makeImageUrl(photoReference, maxWidth);
        }
        return url;
    }

    public static GooglePlaceInfo parseFromJson(JSONObject jsonGooglePlace) {
        try {
            final JSONObject place = !jsonGooglePlace.isNull("result")
                    ? jsonGooglePlace.getJSONObject("result")
                    : jsonGooglePlace;

            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            String photoReference = null;

            if (!place.isNull("photos")) {
                JSONArray photos = place.getJSONArray("photos");
                if (photos.length() > 0) {
                    photoReference = photos.getJSONObject(0).getString("photo_reference");
                }
            }

            return new GooglePlaceInfo(photoReference, lat, lng);

        } catch (JSONException e) {
            Log.e(TAG, "Couldn't parse JSON", e);
        }
        return null;
    }

    public static String makeImageUrl(String photoReference) {
        return makeImageUrl(photoReference, 400);
    }

    public static String makeImageUrl(String photoReference, int maxWidth) {
        if (photoReference == null) {
            return null;
        }
        return String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photoreference=%s&key=%s",
                maxWidth, photoReference, GooglePlaceSearchClient.GOOGLE_PLACES_API_KEY);
    }
}
