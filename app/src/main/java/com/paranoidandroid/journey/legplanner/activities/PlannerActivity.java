package com.paranoidandroid.journey.legplanner.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.fragments.BookmarksPickerFragment;
import com.paranoidandroid.journey.legplanner.fragments.CustomActivityCreatorFragment;
import com.paranoidandroid.journey.legplanner.fragments.DayPlannerFragment;
import com.paranoidandroid.journey.legplanner.fragments.MapViewFragment;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.ui.Day;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.myjourneys.activities.MyJourneysActivity;
import com.paranoidandroid.journey.recommendations.activities.RecommendationsActivity;
import com.paranoidandroid.journey.support.SharedPreferenceUtils;
import com.paranoidandroid.journey.wizard.activities.EditJourneyActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.paranoidandroid.journey.legplanner.fragments.BookmarksPickerFragment.OnBookmarksSelectedListener;
import static com.paranoidandroid.journey.legplanner.fragments.CustomActivityCreatorFragment.OnAddCustomActivityListener;
import static com.paranoidandroid.journey.legplanner.fragments.DayPlannerFragment.DayPlannerListener;
import static com.paranoidandroid.journey.legplanner.fragments.MapViewFragment.MapEventListener;

public class PlannerActivity extends AppCompatActivity implements
        OnAddCustomActivityListener,
        OnBookmarksSelectedListener,
        DayPlannerListener,
        MapEventListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1979;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.menu_yellow) FloatingActionMenu floatingMenu;
    @BindView(R.id.fabZoom) FloatingActionButton fabZoom;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
    @BindView(R.id.drawer) DrawerLayout drawer;
    @BindView(R.id.navigationView) NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;
    private String journeyId;
    private Journey mJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        ButterKnife.bind(this);

        journeyId = getIntent().getExtras().getString("journey_id", "");

        setupToolbar();
        setupDrawer();
        setupFabs();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (checkPlayServices()) {
            fetchJourney();
        }
    }

    private void fetchJourney() {
        if (journeyId.isEmpty()) {
            return;
        }
        ParseQuery<Journey> query = Journey.createQuery();
        query.getInBackground(journeyId, new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    mJourney = journey;
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

    // DayPlannerFragment's DayPlannerListener implementation

    @Override
    public void onLegIndexChanged(int legOrder) {
        // If the map is zoomed in, zoom out first
        // and then change the highlighted marker
        MapViewFragment fragment = getMapViewFragment();
        if (fragment.isZoomed()) {
            fragment.addMarkersFromLegs(mJourney.getLegs());
            fragment.setZoomed(false);
        }
        fragment.changeToMarkerPosition(legOrder);
    }

    @Override
    public void onDayChangedOnSameLeg() {
        // Refresh the markers only if the map is zoomed in
        MapViewFragment fragment = getMapViewFragment();
        if (fragment.isZoomed()) {
            DayPlannerFragment dayPlannerFragment = getDayPlannerFragment();
            fragment.addMarkersFromActivities(dayPlannerFragment.getActivitiesForSelectedDay(), dayPlannerFragment.getSelectedLeg());
        }
    }

    @Override
    public void onActivityRemovedAtIndex(int activityIndex) {
        // Refresh the markers only if the map is zoomed in
        MapViewFragment fragment = getMapViewFragment();
        if (fragment.isZoomed()) {
            DayPlannerFragment dayPlannerFragment = getDayPlannerFragment();
            fragment.addMarkersFromActivities(dayPlannerFragment.getActivitiesForSelectedDay(), dayPlannerFragment.getSelectedLeg());
        }
    }

    // MapViewFragment's MapEventListener implementation

    @Override
    public void onLegMarkerPressedAtIndex(int position) {
        appBar.setExpanded(false);
        getDayPlannerFragment().setSelectedLegPosition(position);
    }

    @Override
    public void onActivityMarkerPressedAtIndex(int position) {
        appBar.setExpanded(false);
        getDayPlannerFragment().scrollToActivityAtIndex(position);
    }

    // CustomActivityCreatorFragment's OnAddCustomActivityListener implementation

    @Override
    public void onAddCustomActivity(String title, Date date, GooglePlace place) {
        final Activity customActivity = Activity.createCustom(title, date, place);
        customActivity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    final DayPlannerFragment dpf = getDayPlannerFragment();
                    dpf.getSelectedLeg().addActivity(customActivity);
                    dpf.getSelectedLeg().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Refresh day planner
                                dpf.refreshCurrentPage();
                                // Refresh map markers
                                getMapViewFragment().addMarkersFromActivities(dpf.getActivitiesForSelectedDay(), dpf.getSelectedLeg());
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // BookmarksPickerFragment's OnBookmarksSelectedListener implementation

    @Override
    public void onBookmarksSelected(List<Bookmark> bookmarks) {
        final List<Activity> activities = Activity.createFromBookmarksForDate(bookmarks, getDayPlannerFragment().getSelectedDay().getDate());
        Activity.saveAllInBackground(activities, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    final DayPlannerFragment dpf = getDayPlannerFragment();
                    dpf.getSelectedLeg().addAllActivities(activities);
                    dpf.getSelectedLeg().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Refresh day planner
                                dpf.refreshCurrentPage();
                                // Refresh map markers
                                getMapViewFragment().addMarkersFromActivities(dpf.getActivitiesForSelectedDay(), dpf.getSelectedLeg());
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public List<Bookmark> getBookmarksList() {
        // TODO only add bookmarks that do not exist as activities on the selected day
        List<Bookmark> bookmarks;
        if ((bookmarks = getDayPlannerFragment().getSelectedLeg().getBookmarks()) == null) {
            return new ArrayList<>();
        }
        return bookmarks;
    }

    // Component setup

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.menu_planner);
        setSupportActionBar(toolbar);
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
                        launchWizard(EditJourneyActivity.EDIT_MODE_TITLE);
                        break;
                    case R.id.miEditLegs:
                        launchWizard(EditJourneyActivity.EDIT_MODE_LEGS);
                        break;
                    case R.id.miEditTags:
                        launchWizard(EditJourneyActivity.EDIT_MODE_TAGS);
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
        Intent intent = EditJourneyActivity.createEditIntent(this, journeyId, editMode);
        startActivity(intent);
    }

    private void setupFabs() {
        floatingMenu.hideMenu(false);
        floatingMenu.setClosedOnTouchOutside(true);
        floatingMenu.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_down));
        floatingMenu.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_to_down));
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(toolbar.getHeight() + Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) { // collapsed
                    floatingMenu.showMenu(true);
                } else  { // expanded
                    floatingMenu.hideMenu(true);
                }
            }
        });
    }

    // FAB click handlers

    // Zoom in (show activities) or zoom out (show legs)

    @OnClick(R.id.fabZoom)
    public void onZoom(View v) {
        MapViewFragment fragment = getMapViewFragment();
        if (fragment.isZoomed()) {
            fragment.addMarkersFromLegs(mJourney.getLegs());
            fragment.setZoomed(false);
        } else {
            DayPlannerFragment dayPlannerFragment = getDayPlannerFragment();
            fragment.addMarkersFromActivities(dayPlannerFragment.getActivitiesForSelectedDay(), dayPlannerFragment.getSelectedLeg());
            fragment.setZoomed(true);
        }
        fabZoom.setImageResource(fragment.isZoomed() ? R.drawable.ic_zoom_out : R.drawable.ic_zoom_in);
    }

    // Navigate to the recommendations part

    @OnClick(R.id.fabAddRecommendation)
    public void exploreRecommendationsPressed(View v) {
        Intent intent = new Intent(PlannerActivity.this, RecommendationsActivity.class);
        intent.putExtra("leg_id", getDayPlannerFragment().getSelectedDay().getLeg().getObjectId());
        startActivity(intent);
        floatingMenu.close(false);
    }

    // Open a bottom sheet to add Bookmarks to the selected day

    @OnClick(R.id.fabAddFromBookmarks)
    public void addFromBookmarksPressed(View v) {
        if (getBookmarksList().size() == 0) {
            Snackbar.make(toolbar, "Tap Explore Recommendations to bookmark your favorite places!", Snackbar.LENGTH_LONG).show();
        } else {
            BookmarksPickerFragment dialog
                    = BookmarksPickerFragment.newInstance(getDayPlannerFragment().getSelectedDay().getDate(), getDayPlannerFragment().getSelectedLeg().getDestination().getCityName());
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            floatingMenu.close(false);
        }
    }

    // Open a dialog to add a custom Activity to the selected day

    @OnClick(R.id.fabAddCustom)
    public void addCustomActivityPressed(View v) {
        Day day = getDayPlannerFragment().getSelectedDay();
        Destination dest = day.getLeg().getDestination();
        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomActivityCreatorFragment newFragment
                = CustomActivityCreatorFragment.newInstance(new LatLng(dest.getLatitude(), dest.getLongitude()), day.getDate(), day.getCity());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        floatingMenu.close(false);
    }

    // Helper methods

    private void showJourney(Journey journey) {
        toolbar.setTitle(journey.getName());
        setDrawerData(journey);
        addDaysToPlanner(journey);
        addMarkersToMap(journey);
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
        getDayPlannerFragment().populateDaysFromLegs(journey.getLegs());
    }

    private MapViewFragment getMapViewFragment() {
        return ((MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map));
    }

    private DayPlannerFragment getDayPlannerFragment() {
        return ((DayPlannerFragment) getSupportFragmentManager().findFragmentById(R.id.legs));
    }

    // Exit the activity if Google Play Services are not installed
    
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                Dialog dialog = googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();
            }
            return false;
        }
        return true;
    }
}
