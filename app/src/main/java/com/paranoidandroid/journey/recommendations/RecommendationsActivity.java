package com.paranoidandroid.journey.recommendations;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.paranoidandroid.journey.R;

import java.util.ArrayList;
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

        List<String> names = Arrays.asList("lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit");

        adapter = RecommendationsPagerAdapter.newInstance(getSupportFragmentManager(), names);
        pager.setAdapter(adapter);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(pager);
            //scrollToTabAfterLayout(current);
        }
    }
}
