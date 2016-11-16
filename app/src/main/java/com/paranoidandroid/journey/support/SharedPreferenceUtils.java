package com.paranoidandroid.journey.support;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utilities for accessing Shared Preferences.
 */
public class SharedPreferenceUtils {
    private static final String SHARED_PREFERENCES_FILE = "com.paranoidandroid.journey.PREFERENCES";

    // For now, store user profile image and name in shared preferences.
    //
    // Ideally, we would be able to store this information along with the current ParseUser, but
    // fields can only be added before login and login has been delegated to Facebook SDK.
    public static void setCurrentUserName(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        prefs.edit().putString("username", name).apply();
    }

    public static String getCurrentUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getString("username", null);
    }

    public static void setCurrentUserImageProfileUri(Context context, String uri) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        prefs.edit().putString("userImageUri", uri).apply();
    }

    public static String getCurrentImageProfileUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getString("userImageUri", null);
    }

    public static void clearUserInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        prefs.edit()
                .remove("username")
                .remove("userImageUri")
                .apply();
    }
}
