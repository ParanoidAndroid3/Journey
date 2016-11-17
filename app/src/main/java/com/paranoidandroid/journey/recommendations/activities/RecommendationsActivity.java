package com.paranoidandroid.journey.recommendations.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsPagerAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendationsActivity extends AppCompatActivity {

    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager pager;
    private RecommendationsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        ButterKnife.bind(this);

        LatLng coordinates = getIntent().getParcelableExtra("coordinates");

        List<Keyword> keywords = Arrays.asList(
                new Keyword(0, "Museums", "museum"), // 0 -> Google
                new Keyword(1, "Food", "4d4b7105d754a06374d81259") // 1 -> Foursquare
        );

        adapter = RecommendationsPagerAdapter.newInstance(getSupportFragmentManager(), keywords, coordinates);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
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
