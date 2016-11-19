package com.paranoidandroid.journey.recommendations.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsListAdapter;
import com.paranoidandroid.journey.recommendations.interfaces.ActivityCreationListener;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;
import com.paranoidandroid.journey.support.ui.SpacesItemDecoration;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
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
    protected String typeTitle;
    protected Date date;
    protected StaggeredGridLayoutManager layoutManager;
    protected RecommendationsListAdapter adapter;
    private Unbinder unbinder;
    private ActivityCreationListener listener;

    protected abstract void search();
    protected abstract EndlessRecyclerViewScrollListener getEndlessScrollListener();
    protected abstract void decorateId(ParseObject activity, String id);

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
        typeTitle = getArguments().getString("typeTitle");
        date = new Date(getArguments().getLong("day_time"));
        adapter = new RecommendationsListAdapter(getActivity(), items, coordinates);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light);
        adapter.setRecommendationsListAdapterClickListener(this);
        rvRecommendations.setAdapter(adapter);
        rvRecommendations.addItemDecoration(new SpacesItemDecoration(16));
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvRecommendations.setLayoutManager(layoutManager);
        rvRecommendations.addOnScrollListener(getEndlessScrollListener());

        loadInitialRecommendations();
    }

    private void loadInitialRecommendations() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                search();
            }
        });
    }

    protected void appendItems(List<? extends Recommendation> places, boolean clearExisting) {
        if (clearExisting) {
            items.clear();
            items.addAll(places);
            adapter.notifyDataSetChanged();
        } else {
            int currentItemCount = items.size();
            items.addAll(places);
            adapter.notifyItemRangeInserted(currentItemCount, places.size());
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAddRecommendationClicked(Recommendation r, boolean saveForLater) {
        final ParseObject activity = ParseObject.create("Activity");
        activity.put("title", r.getName());
        if (!saveForLater) activity.put("date", date);
        activity.put("eventType", typeTitle);
        activity.put("geoPoint", new ParseGeoPoint(r.getLatitude(), r.getLongitude()));
        this.decorateId(activity, r.getId());
        activity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (listener != null)
                        listener.onActivityCreated((Activity) activity);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.Activity){
            this.listener = (ActivityCreationListener) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
