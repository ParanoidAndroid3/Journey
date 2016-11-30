package com.paranoidandroid.journey.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Map tags selected in the wizard to groups of keywords to use in activity recommendations.
 */
public class TagCategoryMapper {
    private static final String TAG = "TagCategoryMapper";

    private static List<RecommendationCategory> sCategories;
    private static Map<String, List<String>> tagsToCategoriesMap = new HashMap<>();
    static {
        sCategories = Arrays.asList(
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Art Galleries", "4bf58dd8d48988d1e2931735"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Museums", "4bf58dd8d48988d181941735"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Food", "4d4b7105d754a06374d81259"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Pubs", "4bf58dd8d48988d11b941735"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Nightlife", "4d4b7105d754a06376d81259"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Recreation", "4d4b7105d754a06377d81259"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Shopping", "4d4b7105d754a06378d81259"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Accommodations", "4bf58dd8d48988d1fa931735"),
                new RecommendationCategory(RecommendationCategory.Source.FOURSQUARE, "Movie Theaters", "4bf58dd8d48988d17f941735")
        );

        tagsToCategoriesMap.put("Foodie",
                Arrays.asList("Food", "Pubs"));
        tagsToCategoriesMap.put("Culture",
                Arrays.asList("Museums", "Art Galleries", "Nightlife"));
        tagsToCategoriesMap.put("Adventure",
                Arrays.asList("Recreation"));
        tagsToCategoriesMap.put("Chiller",
                Arrays.asList("Movie Theaters"));
        //tagsToCategoriesMap.put("Budget", Arrays.asList(""));
        tagsToCategoriesMap.put("Luxury",
                Arrays.asList("Shopping", "Nightlife"));
    }

    private List<String> tags;
    private List<RecommendationCategory> categories;

    public TagCategoryMapper(List<String> tags) {
        if (tags == null) {
            throw new NullPointerException("tags must not be null");
        }
        this.tags = tags;
    }

    public List<RecommendationCategory> getCategories() {
        if (categories == null) {
            categories = makeSortedCategories();
        }
        return categories;
    }

    public List<String> getTags() {
        return tags;
    }

    private List<RecommendationCategory> makeSortedCategories() {
        Set<String> promotedCategories = new HashSet<>();
        for (String tag : tags) {
            if (tagsToCategoriesMap.containsKey(tag)) {
                // Promote all related categories.
                promotedCategories.addAll(tagsToCategoriesMap.get(tag));
            }
        }

        ArrayList<RecommendationCategory> categories = new ArrayList<>(sCategories);
        for (RecommendationCategory category : categories) {
            if (promotedCategories.contains(category.title)) {
                category.setSortOrder(RecommendationCategory.SORT_FIRST);
            } else {
                category.setSortOrder(RecommendationCategory.SORT_LAST);
            }
        }

        Collections.sort(categories);
        return categories;
    }
}
