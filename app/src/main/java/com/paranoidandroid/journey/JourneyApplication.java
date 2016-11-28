package com.paranoidandroid.journey;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.models.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;

/**
 * Application entry point.
 */
public class JourneyApplication extends Application {

    // Enable support for vector drawables in all activities
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String PARSE_APP_ID = "com.github.paranoidandroid3.journey";
    private static final String PARSE_MOUNT = "/parse/";
    private static final String PARSE_URL = "http://journey-paranoid-android.herokuapp.com" + PARSE_MOUNT;

    @Override
    public void onCreate() {
        super.onCreate();

        // Stetho -- for viewing network request with Chrome browser.
        Stetho.initializeWithDefaults(this);

        // Register models
        ParseObject.registerSubclass(Bookmark.class);
        ParseObject.registerSubclass(Activity.class);
        ParseObject.registerSubclass(Destination.class);
        ParseObject.registerSubclass(Journey.class);
        ParseObject.registerSubclass(Leg.class);
        ParseObject.registerSubclass(User.class);

        // Configure parse via a custom builder.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APP_ID)
                .clientKey("9K1CTGjvd77TviQw1THcOBneP6mFKjpb")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .server(PARSE_URL).build());

        // Facebook SDK (via Parse)
        ParseFacebookUtils.initialize(this);
        // Track new installs and when users open this app.
        AppEventsLogger.activateApp(this);
    }
}
