package com.paranoidandroid.journey.wizard.utils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.support.GooglePlaceInfo;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by epushkarskaya on 11/14/16.
 */

public class JourneyBuilder {

    public static final String NAME_KEY = "name";
    public static final String LEGS_KEY = "legs";
    public static final String SIZE_KEY = "size";
    public static final String TAGS_KEY = "tags";

    /**
     * Uses all of the data specified in the wizard to create
     * a brand new Journey and save it to the Parse server.
     *
     * Returns the id of the newly generated Parse Object.
     */
    public static Journey buildJourney(ParseUser creator, Map<String, Object> journeyParts) {
        final Journey journey = new Journey();

        setName(journey, journeyParts);
        setLegs(journey, journeyParts);
        setTags(journey, journeyParts);
        journey.setCreator(creator);

        return journey;
    }

    public static void setName(Journey journey, Map<String, Object> journeyParts) {
        journey.setName((String) journeyParts.get(NAME_KEY));
    }

    public static void setLegs(Journey journey, Map<String, Object> journeyParts) {
        List<Leg> legs = (List<Leg>) journeyParts.get(LEGS_KEY);

        for (Leg leg : legs) {
            journey.addLeg(leg);
        }

    }

    public static void setTags(Journey journey, Map<String, Object> journeyParts) {
        journey.setTripType((String) journeyParts.get(SIZE_KEY));
        journey.setTripTags((List<String>) journeyParts.get(TAGS_KEY));
    }

    public static Destination findDestination(String placeId, String displayName) {

        final Destination destination = new Destination();
        destination.setGooglePlaceId(placeId);
        destination.setDisplayName(generateDisplayName(displayName));

        GooglePlaceSearchClient.getPlaceDetails(placeId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");

                    JSONArray addressComponents = result.getJSONArray("address_components");
                    for (int i = 0; i < addressComponents.length(); i++) {
                        JSONObject component = addressComponents.getJSONObject(i);
                        JSONArray types = component.getJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            if (types.get(j).equals("country")) {
                                destination.setCountryName(component.getString("long_name"));
                                break;
                            } else if (types.get(j).equals("locality")) {
                                destination.setCityName(component.getString("long_name"));
                                break;
                            }
                        }

                    }

                    // use first part of display name if no city is present
                    if (destination.getCityName() == null) {
                        String[] components = destination.getDisplayName().replaceAll("\\s+","").split(",");
                        destination.setCityName(components[0]);
                    }

                    GooglePlaceInfo placeInfo = GooglePlaceInfo.parseFromJson(result);
                    if (placeInfo != null) {
                        destination.setGeoPoint(placeInfo.getLatitude(), placeInfo.getLongitude());
                        if (placeInfo.getPhotoReference() != null) {
                            destination.setGoogleImageReference(placeInfo.getPhotoReference());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        return destination;
    }

    private static String generateDisplayName(String destination) {
        String[] components = destination.split(",");
        if (components.length <= 2) {
            return destination;
        } else if (components[2].equals(" United States")) {
            return components[0] + "," + components[1];
        } else {
            return components[0] + "," + components[components.length -1];
        }
    }

}
