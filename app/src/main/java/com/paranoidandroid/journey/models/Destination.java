package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * One destination in the journey.
 */
@ParseClassName("Destination")
public class Destination extends ParseObject {
    private static final String KEY_CITY_NAME = "cityName";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_COUNTRY_NAME = "countryName";
    private static final String KEY_GEO_POINT = "geoPoint";
    private static final String KEY_GOOGLE_PLACE_ID = "googlePlaceId";
    private static final String KEY_GOOGLE_IMAGE_REFERENCE = "cachedGoogleImageReference";

    public Destination() {
    }

    public Destination(String cityName, String countryName,
            double latitude, double longitude) {
        setCityName(cityName);
        setCountryName(countryName);
        setGeoPoint(latitude, longitude);
    }

    public void setCityName(String name) {
        put(KEY_CITY_NAME, name);
    }

    public String getCityName() {
        return getString(KEY_CITY_NAME);
    }

    public void setCountryName(String name) {
        put(KEY_COUNTRY_NAME, name);
    }

    public String getCountryName() {
        return getString(KEY_COUNTRY_NAME);
    }

    public void setDisplayName(String name) {
        put(KEY_DISPLAY_NAME, name);
    }

    public String getDisplayName() {
        return getString(KEY_DISPLAY_NAME);
    }

    public void setGeoPoint(double latitude, double longitude) {
        put(KEY_GEO_POINT, new ParseGeoPoint(latitude, longitude));
    }

    public ParseGeoPoint getGeoPoint() { return getParseGeoPoint(KEY_GEO_POINT); }

    public double getLatitude() {
        ParseGeoPoint geo = getParseGeoPoint(KEY_GEO_POINT);
        if (geo != null) {
            return geo.getLatitude();
        }
        return 0;
    }

    public double getLongitude() {
        ParseGeoPoint geo = getParseGeoPoint(KEY_GEO_POINT);
        if (geo != null) {
            return geo.getLongitude();
        }
        return 0;
    }

    public void setGooglePlaceId(String id) {
        put(KEY_GOOGLE_PLACE_ID, id);
    }

    public String getGooglePlaceId() {
        return getString(KEY_GOOGLE_PLACE_ID);
    }

    public void setGoogleImageReference(String imageReference) {
        put(KEY_GOOGLE_IMAGE_REFERENCE, imageReference);
    }

    public String getGoogleImageReference() {
        return getString(KEY_GOOGLE_IMAGE_REFERENCE);
    }
}
