package com.paranoidandroid.journey.wizard.models;

/**
 * Created by epushkarskaya on 11/13/16.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This model is used to populate the autocomplete results
 * in the leg planning step of the wizard.
 */
public class AutoCompleteItem {

    String description;
    String placeId;

    public AutoCompleteItem(String description, String placeId) {
        this.description = description;
        this.placeId = placeId;
    }

    public AutoCompleteItem(JSONObject json) throws JSONException {
        this.description = json.getString("description");
        this.placeId = json.getString("place_id");
    }

    public String getDescription() {
        return description;
    }

    public String getPlaceId() {
        return placeId;
    }

}
