package com.paranoidandroid.journey.recommendations.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsListAdapter;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;
import com.paranoidandroid.journey.support.ui.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseRecommendationsFragment extends Fragment implements
        RecommendationsListAdapterClickListener {

    @BindView(R.id.rvRecommendations) RecyclerView rvRecommendations;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeRefreshLayout;
    protected ArrayList<Recommendation> items;
    protected LatLng coordinates;
    protected String keyword;
    private LinearLayoutManager layoutManager;
    private RecommendationsListAdapter adapter;
    private Unbinder unbinder;

    protected abstract void search();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendations_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        coordinates = getArguments().getParcelable("coordinates");
        keyword = getArguments().getString("keyword");
        adapter = new RecommendationsListAdapter(getActivity(), items);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                // TODO: re-populate list or show error if offline
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light);
        adapter.setRecommendationsListAdapterClickListener(this);
        rvRecommendations.setAdapter(adapter);
        rvRecommendations.addItemDecoration(new SpacesItemDecoration(16));
        layoutManager = new LinearLayoutManager(getActivity());
        rvRecommendations.setLayoutManager(layoutManager);
        rvRecommendations.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("RecommendationsFragment", "onLoadMore:"+page);
                // TODO: Load more using each APIs method
            }
        });

        populateRecommendations();
        // TODO: populate initial list
    }

    private void populateRecommendations() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                search();
            }
        });
    }

    protected void appendItems(List<? extends Recommendation> places) {
        items.clear();
        items.addAll(places);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
