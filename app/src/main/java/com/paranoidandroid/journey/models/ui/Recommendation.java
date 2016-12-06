package com.paranoidandroid.journey.models.ui;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Recommendation {

    double lat;
    double lng;
    double rating;
    String address;
    String name;
    String id;
    String imageUrl;
    List<Tip> tips;

    boolean added;
    boolean bookmarked;

    public List<Tip> getTips() {
        return tips;
    }

    public boolean isAdded() {
        return added;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
