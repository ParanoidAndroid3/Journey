package com.paranoidandroid.journey.legplanner.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.fragments.AddActivityFragment;
import com.paranoidandroid.journey.legplanner.fragments.DayViewFragment;
import com.paranoidandroid.journey.legplanner.fragments.MapViewFragment;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.myjourneys.activities.MyJourneysActivity;
import com.paranoidandroid.journey.support.SharedPreferenceUtils;
import com.paranoidandroid.journey.wizard.activities.WizardActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PlannerActivity extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener,
        AddActivityFragment.AddActivityListener {

    @BindView(R.id.fab_add_activity) FloatingActionButton fabAdd;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) DrawerLayout drawer;
    @BindView(R.id.navigationView) NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;
    private boolean mIsMapShowing = true;
    private String journeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        ButterKnife.bind(this);

        journeyId = getIntent().getExtras().getString("journey_id", "");

        setupToolbar();
        setupDrawer();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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

    private void setupDrawer() {
        drawerToggle = setupDrawerToggle(drawer, toolbar);
        drawer.addDrawerListener(drawerToggle);
        setupDrawerContent(navigationView);
    }

    private ActionBarDrawerToggle setupDrawerToggle(DrawerLayout drawer, Toolbar toolbar) {
        return new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miMyJourneys:
                        launchMyJourneys();
                        break;
                    case R.id.miEditTitle:
                        launchWizard(WizardActivity.EDIT_MODE_TITLE);
                        break;
                    case R.id.miEditLegs:
                        launchWizard(WizardActivity.EDIT_MODE_LEGS);
                        break;
                    case R.id.miEditTags:
                        launchWizard(WizardActivity.EDIT_MODE_TAGS);
                        break;
                }
                item.setChecked(false);
                drawer.closeDrawers();
                return true;
            }
        });
    }

    private void launchMyJourneys() {
        Intent intent = new Intent(PlannerActivity.this, MyJourneysActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void launchWizard(int editMode) {
        Intent intent = WizardActivity.createEditIntent(this, journeyId, editMode);
        startActivity(intent);
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
        setDrawerData(journey);
        addMarkersToMap(journey);
        addDaysToPlanner(journey);
    }

    private void setDrawerData(Journey journey) {
        // Title in Drawer.
        MenuItem item = navigationView.getMenu().findItem(R.id.miEditTitle);
        item.setTitle(journey.getName());

        View navHeader = navigationView.getHeaderView(0);

        String userName = SharedPreferenceUtils.getCurrentUserName(this);
        if (userName != null) {
            TextView tvUserName = (TextView) navHeader.findViewById(R.id.tvUserName);
            tvUserName.setText(userName);
        }

        String imageUri = SharedPreferenceUtils.getCurrentImageProfileUri(this);
        if (imageUri != null) {
            ImageView ivProfileImage = (ImageView) navHeader.findViewById(R.id.ivProfileImage);
            Glide.with(this).load(imageUri)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivProfileImage);
        }
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