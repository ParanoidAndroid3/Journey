package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.adapters.ActivitiesListAdapter;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.support.ui.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class DayActivitiesFragment extends Fragment implements
        ActivitiesListAdapter.ActivityAdapterClickListener {

    @BindView(R.id.rvActivities) RecyclerView rvActivities;
    @BindView(R.id.tvNoActivities) TextView tvNoActivities;

    private Unbinder unbinder;
    ArrayList<Activity> items;
    private ActivitiesListAdapter adapter;
    private LinearLayoutManager activitiesLayoutManager;
    private int dayOrder;
    private OnActivitySelectedListener listener;

    @Override
    public void onDeleteActivityAtAdapterIndex(int position) {
        if (this.listener != null) {
            this.listener.onDeleteActivityRequested(items.get(position), position);
        }
    }

    @Override
    public void onSelectActivityAtAdapterIndex(int position) {
        if (this.listener != null) {
            this.listener.onActivitySelected(items.get(position));
        }
    }

    public interface OnActivitySelectedListener {
        void onActivitySelected(Activity activity);
        void onDeleteActivityRequested(Activity activity, int adapterIndex);
        List<Activity> getActivitiesListForDay(int dayOrder);
    }

    public static DayActivitiesFragment newInstance(int dayOrder) {
        Bundle args = new Bundle();
        DayActivitiesFragment fragment = new DayActivitiesFragment();
        args.putInt("day", dayOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        dayOrder = getArguments().getInt("day");
        adapter = new ActivitiesListAdapter(getContext(), items);
        adapter.setActivityAdapterClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvActivities.setAdapter(adapter);
        rvActivities.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvActivities.setItemAnimator(new LandingAnimator());
        activitiesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvActivities.setLayoutManager(activitiesLayoutManager);
        registerForContextMenu(rvActivities);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadActivities();
    }

    private void loadActivities() {
        if (this.listener != null) {
            List<Activity> activities = this.listener.getActivitiesListForDay(dayOrder);
            items.clear();
            items.addAll(activities);
            adapter.notifyDataSetChanged();
            tvNoActivities.setVisibility(activities.size() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    public void notifyItemAdded(Activity activity) {
        items.add(activity);
        adapter.notifyItemInserted(items.size() - 1);
        tvNoActivities.setVisibility(View.GONE);
        scrollToActivityPosition(items.size() - 1);
    }

    public void notifyItemsAdded(List<Activity> activities) {
        int addCount = activities.size();
        int prevCount = items.size();
        items.addAll(activities);
        adapter.notifyItemRangeInserted(prevCount, addCount);
        tvNoActivities.setVisibility(View.GONE);
        scrollToActivityPosition(prevCount + addCount - 1);
    }

    public void notifyItemDeleted(int position) {
        items.remove(position);
        adapter.notifyItemRemoved(position);
        tvNoActivities.setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void scrollToActivityPosition(int position) {
        activitiesLayoutManager.scrollToPosition(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActivitySelectedListener) {
            this.listener = (OnActivitySelectedListener) context;
        } else if (getParentFragment() instanceof OnActivitySelectedListener) {
            this.listener = (OnActivitySelectedListener) getParentFragment();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnActivitySelectedListener");
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
