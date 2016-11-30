package com.paranoidandroid.journey.recommendations.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.adapters.RecommendationsListAdapter;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationActivityListener;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.RecommendationCategory;
import com.paranoidandroid.journey.support.ui.EndlessRecyclerViewScrollListener;
import com.paranoidandroid.journey.support.ui.SpacesItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseRecommendationsFragment extends Fragment implements
        RecommendationsListAdapterClickListener {

    @BindView(R.id.rvRecommendations) RecyclerView rvRecommendations;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeRefreshLayout;
    protected LatLng coordinates;
    protected RecommendationCategory category;
    protected StaggeredGridLayoutManager layoutManager;
    private ArrayList<Recommendation> items;
    private RecommendationsListAdapter adapter;
    private Unbinder unbinder;
    private RecommendationActivityListener listener;

    // Abstract method to search for items on the API
    protected abstract void search();
    // Abstract method to provide endless scrolling for the API
    protected abstract EndlessRecyclerViewScrollListener getScrollListener();

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
        category = Parcels.unwrap(getArguments().getParcelable("category"));
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
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        adapter.setRecommendationsListAdapterClickListener(this);
        rvRecommendations.setAdapter(adapter);
        rvRecommendations.addItemDecoration(new SpacesItemDecoration(16));
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvRecommendations.setLayoutManager(layoutManager);
        rvRecommendations.addOnScrollListener(getScrollListener());

        loadRecommendations();
    }

    private void loadRecommendations() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                search();
            }
        });
    }

    // Called from descendants when new Recommendations are received

    protected void appendItems(List<? extends Recommendation> places, boolean clearExisting) {
        if (listener != null) {
            listener.decorateRecommendations(places, category.source.getSourceId());
        }

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

    // RecommendationsListAdapterClickListener implementation

    @Override
    public void onAddBookmarkClicked(Recommendation r, final int position) {
        listener.onBookmarkRecommendation(r, category, new RecommendationActivityListener.OnRecommendationSaveListener() {
            @Override
            public void onSaved() {
                items.get(position).setBookmarked(true);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onError() {
                Snackbar.make(swipeRefreshLayout, "Error adding bookmark!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveBookmarkClicked(Recommendation r, final int position) {
        listener.onUnBookmarkRecommendation(r, category, new RecommendationActivityListener.OnRecommendationSaveListener() {
            @Override
            public void onSaved() {
                items.get(position).setBookmarked(false);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onError() {
                Snackbar.make(swipeRefreshLayout, "Error removing bookmark!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // Lifecycle methods

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.Activity){
            this.listener = (RecommendationActivityListener) context;
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
