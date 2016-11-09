package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * An authenticated Journey user.
 *
     User {
         id
         name
         googleUserId
         facebookUserId
         profileImageUrl
     }
 */
@ParseClassName("User")
public class User extends ParseUser {
    private static final String KEY_PROFILE_IMAGE_URL = "profileImageUrl";

    public User() {
    }

    public void setProfileImageUrl(String url) {
        put(KEY_PROFILE_IMAGE_URL, url);
    }

    public String getProfileImageUrl() {
        return getString(KEY_PROFILE_IMAGE_URL);
    }
}
