package com.paranoidandroid.journey.recommendations.interfaces;

import com.paranoidandroid.journey.models.ui.Recommendation;

public interface RecommendationsListAdapterClickListener {
    void onAddRecommendationClicked(Recommendation r, boolean saveForLater);
}
