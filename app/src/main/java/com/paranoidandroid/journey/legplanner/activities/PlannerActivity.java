package com.paranoidandroid.journey.legplanner.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.paranoidandroid.journey.legplanner.fragments.AddActivityFragment;
import com.paranoidandroid.journey.legplanner.fragments.DayViewFragment;
import com.paranoidandroid.journey.legplanner.fragments.MapViewFragment;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlannerActivity extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener,
        AddActivityFragment.AddActivityListener {

    @BindView(R.id.fab_add_activity) FloatingActionButton fabAdd;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean mIsMapShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        ButterKnife.bind(this);

        setupToolbar();
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

    // Switch view menu

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

    // AddActivityListener implementation

    @Override
    public void onCustomActivityAdded(String title) {
        getDayViewFragment().addCustomActivity(title);
    }

    // Component setup

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.menu_planner);
        toolbar.setOnMenuItemClickListener(this);
    }

    // Show / Hide fragment methods

    protected void showDayView() {
        showFab();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(getDayViewFragment());
        ft.hide(getMapViewFragment());
        ft.commit();
    }

    protected void showMapView() {
        hideFab();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(getMapViewFragment());
        ft.hide(getDayViewFragment());
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
        fabAdd.hide();
    }

    private void showFab() {
        fabAdd.show();
    }


}