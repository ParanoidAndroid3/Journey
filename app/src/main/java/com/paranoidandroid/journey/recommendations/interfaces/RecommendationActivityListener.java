package com.paranoidandroid.journey.recommendations.interfaces;

import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.support.RecommendationCategory;

import java.util.List;

public interface RecommendationActivityListener {

    void onBookmarkRecommendation(Recommendation rec, RecommendationCategory category, OnRecommendationSaveListener listener);
    void onUnBookmarkRecommendation(Recommendation rec, RecommendationCategory category, OnRecommendationSaveListener listener);
    void decorateRecommendations(List<? extends Recommendation> places, String s);

    interface OnRecommendationSaveListener {
        void onSaved();
        void onError();
    }
}
