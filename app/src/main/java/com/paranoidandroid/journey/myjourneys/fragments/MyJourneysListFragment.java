package com.paranoidandroid.journey.myjourneys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paranoidandroid.journey.myjourneys.adapters.JourneyAdapter;
import com.paranoidandroid.journey.models.Journey;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Load and display a list of the user's journey's.
 */
public class MyJourneysListFragment extends Fragment
        implements JourneyAdapter.OnItemSelectedListener{
    private static final String TAG = "MyJourneysListFragment";

    public interface OnJourneySelectedListener {
        void onJourneySelected(Journey journey);
        void onCreateNewJourney();
    }

    private com.paranoidandroid.journey.databinding.FragmentMyJourneysBinding binding;
    private JourneyAdapter adapter;
    private OnJourneySelectedListener listener;

    public static MyJourneysListFragment newInstance() {
        return new MyJourneysListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JourneyAdapter(new ArrayList<Journey>());
        adapter.setOnJourneySelectedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = com.paranoidandroid.journey.databinding.FragmentMyJourneysBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvJourneys.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvJourneys.setAdapter(adapter);
        binding.setListener(listener);

        fetchJourneys();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJourneySelectedListener) {
            listener = (OnJourneySelectedListener) context;
        } else {
            throw new IllegalArgumentException(
                    "context must implement " + OnJourneySelectedListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onItemSelected(Journey journey) {
        if (listener != null) {
            listener.onJourneySelected(journey);
        }
    }

    private void showInitialLoadProgressBar() {
        binding.pbInitialLoad.setVisibility(View.VISIBLE);
        binding.rvJourneys.setVisibility(View.GONE);
    }

    private void hideInitialLoadProgressBar() {
        binding.pbInitialLoad.setVisibility(View.GONE);
    }

    private void showJourneys(List<Journey> journeys) {
        if (journeys.size() == 0) {
            // Show empty journey view.
            binding.rlEmptyView.setVisibility(View.VISIBLE);
            binding.rvJourneys.setVisibility(View.GONE);
        } else {
            binding.rlEmptyView.setVisibility(View.GONE);
            binding.rvJourneys.setVisibility(View.VISIBLE);
            adapter.addAll(journeys);
        }
    }

    protected void fetchJourneys() {
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.include("legs");
        query.include("legs.destination");

        // TODO(emmanuel): think about security settings so users can't view all other users' data by default.
        //query.whereEqualTo("creator", ParseUser.getCurrentUser());

        showInitialLoadProgressBar();

        query.findInBackground(new FindCallback<Journey>() {
            @Override
            public void done(List<Journey> objects, ParseException e) {
                hideInitialLoadProgressBar();

                if (e == null) {
                    showJourneys(objects);
                } else {
                    // TODO(emmanuel): show offline message.
                    Toast.makeText(getContext(),
                            "Error retrieving journeys", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch journeys: ", e);
                }
            }
        });
    }
}
