package com.paranoidandroid.journey.models;

import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final String KEY_GOOGLE = "google_id";
    private static final String KEY_FOURSQUARE = "foursquare_id";
    private static final String KEY_IMAGE_URL = "imageURL";

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

    public void setGoogleId(String googleId) {
        put(KEY_GOOGLE, googleId);
    }

    public String getGoogleId() {
        return getString(KEY_GOOGLE);
    }

    public void setFoursquareId(String foursquareId) {
        put(KEY_FOURSQUARE, foursquareId);
    }

    public String getFoursquareId() {
        return getString(KEY_FOURSQUARE);
    }

    public void setImageUrl(String url) { put(KEY_IMAGE_URL, url); }

    public String getImageUrl() { return getString(KEY_IMAGE_URL); }

    public static Activity createCustom(String title, Date date, GooglePlace place) {
        Activity customActivity = new Activity();
        if (place.getImageUrl() != null)
            customActivity.setImageUrl(place.getImageUrl());
        customActivity.setTitle(title);
        customActivity.setDate(date);
        customActivity.setEventType(place.getName());
        customActivity.setGoogleId(place.getId());
        customActivity.setGeoPoint(place.getLatitude(), place.getLongitude());
        return customActivity;
    }

    public static List<Activity> createFromBookmarksForDate(List<Bookmark> bookmarks, Date date) {
        List<Activity> activities = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            Activity activity = new Activity();
            if (b.getImageUrl() != null)
                activity.setImageUrl(b.getImageUrl());
            if (b.getGoogleId() != null)
                activity.setGoogleId(b.getGoogleId());
            if (b.getFoursquareId() != null)
                activity.setFoursquareId(b.getFoursquareId());
            activity.setTitle(b.getTitle());
            activity.setDate(date);
            activity.setEventType(b.getEventType());
            activity.setGeoPoint(b.getLatitude(), b.getLongitude());
            activities.add(activity);
        }
        return activities;
    }
}
