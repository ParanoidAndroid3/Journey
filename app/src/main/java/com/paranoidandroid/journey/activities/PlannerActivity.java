package com.paranoidandroid.journey.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.fragments.DayViewFragment;
import com.paranoidandroid.journey.fragments.MapViewFragment;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.GooglePlace;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.network.FoursquareVenueSearchClient;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlannerActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private FloatingActionButton btAddActivity;
    private boolean mIsMapShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        setupToolbar();
        setupFab();
        showMapView();
        fetchJourney();
    }

    private void fetchJourney() {
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.include("legs");
        query.include("legs.destination");
        query.include("legs.activities");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground("ZIvsEb6kxY", new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    showJourney(journey);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.layoutType) {
            if (mIsMapShowing) { showDayView(); }
            else { showMapView(); }
            mIsMapShowing = !mIsMapShowing;
            item.setTitle(mIsMapShowing ? "Days" : "Map");
        }
        return false;
    }

    // Component setup

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_planner);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void setupFab() {
        btAddActivity = (FloatingActionButton) findViewById(R.id.fab_add_activity);
    }

    // Show / Hide fragment methods

    protected void showDayView() {
        btAddActivity.show();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(getDayViewFragment());
        ft.hide(getMapViewFragment());
        ft.commit();
    }

    protected void showMapView() {
        btAddActivity.hide();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(getMapViewFragment());
        ft.hide(getDayViewFragment());
        ft.commit();
    }

    // Handlers

    public void addActivityPressed(View view) {
        // TODO: Open activity explorer
        Leg leg = getDayViewFragment().getSelectedLeg();
        Destination d = leg.getDestination();
        GooglePlaceSearchClient.search(d.getLatitude(), d.getLongitude(), "museum", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<GooglePlace> places = GooglePlace.parseJSON(response);
            }
        });
        /*
        FoursquareVenueSearchClient.search(d.getLatitude(), d.getLongitude(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject res = response.getJSONObject("response");
                    JSONArray groups = res.getJSONArray("groups");
                    JSONArray items = groups.getJSONObject(0).getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        System.out.println(items.getJSONObject(i).getJSONObject("venue").getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        */
    }

    // Helper methods

    private void showJourney(Journey journey) {
        addMarkersToMap(journey);
        addDaysToPlanner(journey);
    }

    private void addMarkersToMap(Journey journey) {
        getMapViewFragment().addMarkersFromLegs(journey.getLegs());
    }

    private void addDaysToPlanner(Journey journey) {
        getDayViewFragment().populateDaysFromLegs(journey.getLegs());
    }

    private MapViewFragment getMapViewFragment() {
        return ((MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map));
    }

    private DayViewFragment getDayViewFragment() {
        return ((DayViewFragment) getSupportFragmentManager().findFragmentById(R.id.legs));
    }
}