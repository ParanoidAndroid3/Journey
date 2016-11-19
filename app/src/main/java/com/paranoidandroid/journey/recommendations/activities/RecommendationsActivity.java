package com.paranoidandroid.journey.recommendations.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsPagerAdapter;
import com.paranoidandroid.journey.recommendations.interfaces.ActivityCreationListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendationsActivity extends AppCompatActivity implements
        ActivityCreationListener {

    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager pager;
    private RecommendationsPagerAdapter adapter;
    private Leg leg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        ButterKnife.bind(this);

        LatLng coordinates = getIntent().getParcelableExtra("coordinates");
        long dayTime = getIntent().getLongExtra("day_date", -1);
        String legId = getIntent().getStringExtra("leg_id");

        fetchLeg(legId);

        List<Keyword> keywords = Arrays.asList(
                new Keyword(0, "Museums", "museum"), // 0 -> Google
                new Keyword(1, "Food", "4d4b7105d754a06374d81259") // 1 -> Foursquare
        );

        adapter = RecommendationsPagerAdapter.newInstance(getSupportFragmentManager(), keywords, coordinates, dayTime);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    private void fetchLeg(String legId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Leg");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(legId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d("RecommendationsActivity", "Leg fetched in background");
                    leg = (Leg) object;
                } else {
                    Log.e("RecommendationsActivity", "Error fetching leg");
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Activity activity) {
        leg.addActivity(activity);
        leg.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("RecommendationsActivity", "Activity saved!");
                } else {
                    Log.d("RecommendationsActivity", "Error saving Activity");
                    e.printStackTrace();
                }
            }
        });
    }

    // Temp stub to map activity categories to keywords for the different APIs

    public class Keyword {
        public String tabTitle;
        public String keyword;
        public int type;

        public Keyword(int type, String title, String keyword) {
            this.type = type;
            this.tabTitle = title;
            this.keyword = keyword;
        }
    }
}
