package com.paranoidandroid.journey.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.paranoidandroid.journey.fragments.DayViewFragment;
import com.paranoidandroid.journey.fragments.MapViewFragment;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
        // TODO: pin the Journey and retrieve from db instead
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Journey");
        query.include("legs");
        query.include("legs.destination");
        query.include("legs.activities");

        query.getInBackground("ZIvsEb6kxY", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Journey journey =  (Journey) object;
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

    public void addActivityPressed() {
        // TODO: Open activity explorer
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