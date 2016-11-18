package com.paranoidandroid.journey.models.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GooglePlace extends Recommendation{
    String id;
    double lat;
    double lng;
    String name;
    String image_url_reference;
    double rating;
    String address;

    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getRating() { return rating; }
    public double getLatitude() { return lat; }
    public double getLongitude() { return lng; }

    public String getImageURL() {
        if (this.image_url_reference == null) {
            return null;
        }
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + this.image_url_reference
                + "&key=AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y";
    }

    private static GooglePlace parsePlace(JSONObject jsonPlace) throws JSONException {
        GooglePlace gp = new GooglePlace();
        JSONObject location = jsonPlace.getJSONObject("geometry").getJSONObject("location");
        gp.id = jsonPlace.getString("id");
        gp.name = jsonPlace.getString("name");
        gp.lat = location.getDouble("lat");
        gp.lng = location.getDouble("lng");
        gp.address = jsonPlace.getString("vicinity");
        gp.rating = (!jsonPlace.isNull("rating")) ? jsonPlace.getDouble("rating") : -1.;
        if (!jsonPlace.isNull("photos"))
            gp.image_url_reference = jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        return gp;
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
