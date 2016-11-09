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
    private static final String KEY_COUNTRY_NAME = "countryName";
    private static final String KEY_GEO_POINT = "geoPoint";

    public Destination() {
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

    public void setGeoPoint(double latitude, double longitude) {
        put(KEY_GEO_POINT, new ParseGeoPoint(latitude, longitude));
    }

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
}
