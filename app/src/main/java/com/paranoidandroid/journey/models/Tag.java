package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Trip category.
 */
@ParseClassName("Tag")
public class Tag extends ParseObject {
    private static final String KEY_NAME = "name";

    public Tag() {
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
    }
}
