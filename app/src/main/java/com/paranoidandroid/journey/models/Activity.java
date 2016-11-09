package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.Date;

/**
 * An activity or event that will be added to the plan (e.g. "Visit Natural History Museum").
 */
// TODO: should we call this something different just to avoid confusion with Android's Activity?
@ParseClassName("Activity")
public class Activity extends ParseObject {
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_GEO_POINT = "geoPoint";
    private static final String KEY_EVENT_TYPE = "eventType";

    public Activity() {
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setDate(Date date) {
        if (date == null) {
            put(KEY_DATE, JSONObject.NULL);
        } else {
            put(KEY_DATE, date);
        }
    }

    public Date getDate() {
        return getDate(KEY_DATE);
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

    public void setEventType(String eventType) {
        put(KEY_EVENT_TYPE, eventType);
    }

    public String getEventType() {
        return getString(KEY_EVENT_TYPE);
    }
}
