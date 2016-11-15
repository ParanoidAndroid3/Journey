package com.paranoidandroid.journey.wizard.utils;

import java.util.Map;

/**
 * Created by epushkarskaya on 11/14/16.
 */

public class JourneyBuilderUtils {

    public static final String NAME_KEY = "name";
    public static final String LEGS_KEY = "legs";
    public static final String SIZE_KEY = "size";
    public static final String TAGS_KEY = "tags";

    /**
     * Uses all of the data specified in the wizard to create
     * a Journey and save it to the Parse server.
     *
     * Returns the id of the newly generated Parse Object.
     */
    public static int buildJourney(Map<String, Object> journeyParts) {
        // todo: build new journey parse object, save to server
        // todo: return id of journey
        return 0;
    }
}
