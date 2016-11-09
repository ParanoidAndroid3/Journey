package com.paranoidandroid.journey;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;

/**
 * Application entry point.
 */
public class JourneyApplication extends Application {

    private static final String PARSE_APP_ID = "com.github.paranoidandroid3.journey";
    private static final String PARSE_MOUNT = "/parse/";
    private static final String PARSE_URL = "http://journey-paranoid-android.herokuapp.com" + PARSE_MOUNT;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        // Configure parse via a custom builder.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APP_ID)
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .server(PARSE_URL).build());
    }
}
