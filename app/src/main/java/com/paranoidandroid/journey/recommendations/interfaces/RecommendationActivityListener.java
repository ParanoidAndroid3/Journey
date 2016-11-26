package com.paranoidandroid.journey.recommendations.interfaces;

import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.models.ui.Day;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;

import java.util.Date;
import java.util.List;

public interface RecommendationActivityListener {

    void onBookmarkRecommendation(Recommendation rec, RecommendationsActivity.Keyword keyword, OnRecommendationSaveListener listener);
    void onUnBookmarkRecommendation(Recommendation rec, RecommendationsActivity.Keyword keyword, OnRecommendationSaveListener listener);
    void decorateRecommendations(List<? extends Recommendation> places, String s);

    interface OnRecommendationSaveListener {
        void onSaved();
        void onError();
    }
}
