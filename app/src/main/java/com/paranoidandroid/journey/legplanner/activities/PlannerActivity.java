package com.paranoidandroid.journey.legplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.legplanner.fragments.AddActivityFragment;
import com.paranoidandroid.journey.legplanner.fragments.DayViewFragment;
import com.paranoidandroid.journey.legplanner.fragments.MapViewFragment;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlannerActivity extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener,
        AddActivityFragment.AddActivityListener {

    @BindView(R.id.fab_add_activity) FloatingActionButton fabAddActivity;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean isMapShowing = true;
    private String journeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        ButterKnife.bind(this);

        journeyId = getIntent().getExtras().getString("journey_id", "");

        setupToolbar();
        showMapView();
        fetchJourney();
    }

    private void fetchJourney() {
        if (journeyId.isEmpty()) {
            Log.e("PlannerActivity", "No journey id provided!");
            return;
        }
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.include("legs");
        query.include("legs.destination");
        query.include("legs.activities");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(journeyId, new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    showJourney(journey);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Switch view menu

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.layoutType) {
            if (isMapShowing) { showDayView(); }
            else { showMapView(); }
            isMapShowing = !isMapShowing;
            item.setTitle(isMapShowing ? "Days" : "Map");
        }
        return false;
    }

    // AddActivityListener implementation

    @Override
    public void onCustomActivityAdded(String title) {
        getDayViewFragment().addCustomActivity(title);
    }

    @Override
    public void onRecommendationActivityClicked() {
        Leg leg = getDayViewFragment().getSelectedDay().getLeg();
        LatLng latLng = new LatLng(leg.getDestination().getLatitude(), leg.getDestination().getLongitude());

        Intent intent = new Intent(PlannerActivity.this, RecommendationsActivity.class);
        intent.putExtra("coordinates", latLng);
        startActivity(intent);
    }

    // Component setup

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.menu_planner);
        toolbar.setOnMenuItemClickListener(this);
    }

    // Show / Hide fragment methods

    protected void showDayView() {
        showFab();
        swapFragment(getMapViewFragment(), getDayViewFragment());
    }

    protected void showMapView() {
        hideFab();
        swapFragment(getDayViewFragment(), getMapViewFragment());
    }

    private void swapFragment(Fragment toHide, Fragment toShow) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(toShow);
        ft.hide(toHide);
        ft.commit();
    }

    // FAB Click handler

    public void addActivityPressed(View view) {
        AddActivityFragment dialog = AddActivityFragment.newInstance();
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    // Helper methods

    private void showJourney(Journey journey) {
        toolbar.setTitle(journey.getName());
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

    private void hideFab() {
        fabAddActivity.hide();
    }

    private void showFab() {
        fabAddActivity.show();
    }


}