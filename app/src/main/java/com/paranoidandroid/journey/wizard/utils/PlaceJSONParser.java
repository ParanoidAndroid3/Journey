package com.paranoidandroid.journey.wizard.utils;

/**
 * Created by epushkarskaya on 11/19/16.
 */

import com.paranoidandroid.journey.wizard.models.LegItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<LegItem> parse(JSONObject jObject){

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    private List<LegItem> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<LegItem> placesList = new ArrayList<>();

        /** Taking each place, parses and adds to list object */
        for(int i = 0; i < placesCount; i++){
            try {
                LegItem place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    /** Parsing the Place JSON object */
    private LegItem getPlace(JSONObject jPlace){
        try {
            LegItem place = new LegItem();
            place.setDestination(jPlace.getString("description"));
            place.setPlacesId(jPlace.getString("place_id"));
            return place;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
