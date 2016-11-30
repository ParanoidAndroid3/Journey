package com.paranoidandroid.journey.models;

import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.Date;

/**
 * A bookmark place that can be added to the plan
 */
@ParseClassName("Bookmark")
public class Bookmark extends ParseObject {
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_GEO_POINT = "geoPoint";
    private static final String KEY_EVENT_TYPE = "eventType";
    private static final String KEY_GOOGLE = "google_id";
    private static final String KEY_FOURSQUARE = "foursquare_id";
    private static final String KEY_IMAGE_URL = "imageURL";

    public Bookmark() {
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

    public static Bookmark createFromRecommendation(Recommendation rec, RecommendationCategory category) {
        Bookmark bookmark = new Bookmark();
        if (rec.getImageUrl() != null)
            bookmark.setImageUrl(rec.getImageUrl());
        bookmark.put("title", rec.getName());
        bookmark.put("eventType", category.title);
        bookmark.put("geoPoint", new ParseGeoPoint(rec.getLatitude(), rec.getLongitude()));
        bookmark.put(category.source.getSourceId(), rec.getId());
        return bookmark;
    }
}
