package com.paranoidandroid.journey.models.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class GooglePlace extends Recommendation {

    public static String makeImageUrl(String image_url_reference) {
        if (image_url_reference == null) {
            return null;
        }
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + image_url_reference
                + "&key=AIzaSyDoES56ptzOPbwy62kw9JMT4zBgloJQD7Y";
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
        if (!jsonPlace.isNull("photos"))
            gp.imageUrl = makeImageUrl(jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
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
