package com.paranoidandroid.journey.recommendations.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsPagerAdapter;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationActivityListener;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.paranoidandroid.journey.support.TagCategoryMapper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class RecommendationsActivity extends AppCompatActivity implements
        RecommendationActivityListener {

    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager pager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvCity) TextView tvCity;
    @BindView(R.id.ivBackdrop) ImageView ivBackdrop;

    private RecommendationsPagerAdapter adapter;
    private Leg leg;
    String legId;

    TagCategoryMapper tagCategoryMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        ButterKnife.bind(this);

        setupToolbar();

        legId = getIntent().getStringExtra("leg_id");
        List<String> tags = getIntent().getStringArrayListExtra("tags");
        tagCategoryMapper = new TagCategoryMapper(tags);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLeg(legId);
    }

    // Get the Leg with Activities and Bookmarks from Parse

    private void fetchLeg(String legId) {
        ParseQuery<Leg> query = ParseQuery.getQuery(Leg.class);
        query.include("activities");
        query.include("bookmarks");
        query.include("destination");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.getInBackground(legId, new GetCallback<Leg>() {
            public void done(Leg object, ParseException e) {
                if (e == null) {
                    leg = object;
                    setupTabs();
                    tvCity.setText(leg.getDestination().getCityName());
                    loadPhotoFromGooglePlaceId(leg.getDestination().getGooglePlaceId());
                } else {
                    e.printStackTrace();
                    Snackbar.make(tabLayout, "Error loading recommendations!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Get backdrop image from Google Place API using the Place ID saved in Leg

    private void loadPhotoFromGooglePlaceId(final String googlePlaceId) {
        GooglePlaceSearchClient.findDetails(googlePlaceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                GooglePlace googlePlace = GooglePlace.parsePlaceDetails(response);
                if (googlePlace != null)
                    loadBackdrop(googlePlace.getImageUrl());
            }
        });
    }

    private void loadBackdrop(String url) {
        if (url != null) {
            Glide.with(this).load(url).centerCrop().into(ivBackdrop);
        }
    }

    // RecommendationActivityListener implementation

    // Find the leg Bookmarks that are common with the Recommendations received from Google/Foursquare APIs

    @Override
    public void decorateRecommendations(List<? extends Recommendation> places, String s) {
        Set<String> bookmarkIds = new HashSet<>();
        if (leg.getBookmarks() != null) {
            try {
                // Need to call this synchronously to avoid 'Object not found' error
                Bookmark.fetchAllIfNeeded(leg.getBookmarks());
                for (Bookmark bookmark : leg.getBookmarks()) {
                    bookmarkIds.add((String) bookmark.get(s));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < places.size(); i++) {
            if (bookmarkIds.contains(places.get(i).getId())) places.get(i).setBookmarked(true);
        }
    }

    // Create a Bookmark object from the Recommendation attributes and save it to Parse

    @Override
    public void onBookmarkRecommendation(
            Recommendation rec,
            RecommendationCategory category,
            final OnRecommendationSaveListener listener) {
        if (listener == null) {
            Snackbar.make(tabLayout, "Error adding bookmark!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (leg == null) {
            listener.onError();
            return;
        }
        final Bookmark bookmark = Bookmark.createFromRecommendation(rec, category);
        bookmark.saveInBackground(new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                if (e == null) {
                    leg.addBookmark(bookmark);
                    leg.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                listener.onSaved();
                            } else {
                                e.printStackTrace();
                                listener.onError();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    listener.onError();
                }
            }
        });
    }

    // Find the Bookmark in the Leg and remove it

    @Override
    public void onUnBookmarkRecommendation(
            Recommendation rec,
            RecommendationCategory category,
            final OnRecommendationSaveListener listener) {
        if (listener == null) {
            Snackbar.make(tabLayout, "Error adding bookmark!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (leg == null) {
            listener.onError();
            return;
        }
        try {
            // Need to call this synchronously to avoid 'Object not found' error
            Bookmark.fetchAllIfNeeded(leg.getBookmarks());
            for (Bookmark bookmark : leg.getBookmarks()) {
                if (rec.getId().equals(bookmark.get(category.source.getSourceId()))) {
                    leg.removeBookmark(bookmark);
                    leg.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                listener.onSaved();
                                // TODO: Remove bookmark object from parse as well
                            } else {
                                e.printStackTrace();
                                listener.onError();
                            }
                        }
                    });
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            listener.onError();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current leg id
        savedInstanceState.putString("LEG_ID", legId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore leg id
        legId = savedInstanceState.getString("LEG_ID");
    }

    // Component setup

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupTabs() {
        LatLng source = new LatLng(leg.getDestination().getLatitude(), leg.getDestination().getLongitude());
        adapter = RecommendationsPagerAdapter.newInstance(getSupportFragmentManager(), tagCategoryMapper.getCategories(), source);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
