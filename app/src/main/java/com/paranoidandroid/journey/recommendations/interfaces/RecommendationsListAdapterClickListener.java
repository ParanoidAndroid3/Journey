package com.paranoidandroid.journey.recommendations.interfaces;

import com.paranoidandroid.journey.models.ui.Recommendation;

public interface RecommendationsListAdapterClickListener {
    void onAddBookmarkClicked(Recommendation r, int adapterIndex);
    void onRemoveBookmarkClicked(Recommendation recommendation, int position);
}
