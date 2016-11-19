package com.paranoidandroid.journey.models.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoursquareVenue extends Recommendation{

    String image_url_prefix;
    String image_url_suffix;

    @Override
    public String getImageURL() {
        if (this.image_url_prefix == null || this.image_url_suffix == null) {
            return null;
        }
        return this.image_url_prefix
                + "cap500"
                + this.image_url_suffix;
    }

    private static FoursquareVenue parseVenue(JSONObject jsonPlace) throws JSONException {
        FoursquareVenue fv = new FoursquareVenue();
        JSONObject venue = jsonPlace.getJSONObject("venue");
        fv.id = venue.getString("id");
        fv.name = venue.getString("name");
        JSONObject location = venue.getJSONObject("location");
        fv.lat = location.getDouble("lat");
        fv.lng = location.getDouble("lng");
        fv.address = location.getString("formattedAddress");
        fv.rating = (!venue.isNull("rating")) ? venue.getDouble("rating") : -1.;
        JSONObject photos = venue.getJSONObject("photos");
        JSONArray groups = photos.getJSONArray("groups");
        JSONObject group = groups.getJSONObject(0); // assume we have at least one group
        JSONArray items = group.getJSONArray("items");
        JSONObject item = items.getJSONObject(0); // assume we have at least one item
        fv.image_url_prefix = item.getString("prefix");
        fv.image_url_suffix = item.getString("suffix");
        return fv;
    }

    public static List<FoursquareVenue> parseJSON(JSONObject response) {
        List<FoursquareVenue> list = new ArrayList<>();
        try {
            JSONObject res = response.getJSONObject("response");
            JSONArray groups = res.getJSONArray("groups");
            JSONObject group = groups.getJSONObject(0); // assume we have at least one group
            JSONArray items = group.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                list.add(parseVenue(items.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
