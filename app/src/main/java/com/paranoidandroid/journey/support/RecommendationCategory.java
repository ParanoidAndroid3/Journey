package com.paranoidandroid.journey.support;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

/**
 * A single category of recommendations, e.g. museums, restaurants, etc.
 */
@Parcel
public class RecommendationCategory implements Comparable<RecommendationCategory> {
    public static final int SORT_LAST = 1;
    public static final int SORT_FIRST = 0;

    /**
     * Specifies the source of a recommendation category.
     */
    public enum Source {
        GOOGLE("google_id"),
        FOURSQUARE("foursquare_id");

        private String sourceId;

        Source(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getSourceId() {
            return sourceId;
        }
    }

    public final Source source;
    public final String title;
    public final String id;

    private int sortOrder;

    public RecommendationCategory() {
        // Default constructor for Parceler.
        source = null;
        title = null;
        id = null;
    }

    // TODO(emmanuel): title should be a string id to support localization.
    public RecommendationCategory(
            Source source, String title, String id) {
        this.source = source;
        this.title = title;
        this.id = id;
        this.sortOrder = SORT_LAST;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int compareTo(@NonNull RecommendationCategory category) {
        if (this.sortOrder != category.sortOrder) {
            return this.sortOrder - category.sortOrder;
        }

        String myTitle = this.title == null ? "" : this.title;
        String theirTitle = category.title == null ? "" : category.title;
        return myTitle.compareTo(theirTitle);
    }
}
