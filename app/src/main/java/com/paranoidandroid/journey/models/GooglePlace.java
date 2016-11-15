package com.paranoidandroid.journey.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GooglePlace {
    String id;
    double lat;
    double lng;
    String name;
    String image_url_reference;
    double rating;
    String address;

    public String getImageURL() {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + this.image_url_reference
                + "&key=AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y";
    }

    private static GooglePlace parsePlace(JSONObject jsonPlace) throws JSONException {
        GooglePlace gp = new GooglePlace();
        JSONObject location = jsonPlace.getJSONObject("geometry").getJSONObject("location");
        gp.id = jsonPlace.getString("id");
        gp.lat = location.getDouble("lat");
        gp.lng = location.getDouble("lng");
        gp.image_url_reference = jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        gp.name = jsonPlace.getString("name");
        gp.rating = jsonPlace.getDouble("rating");
        gp.address = jsonPlace.getString("vicinity");
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
}
