package com.paranoidandroid.journey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paranoidandroid.journey.databinding.FragmentMyJourneysBinding;
import com.paranoidandroid.journey.models.Journey;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Load and display a list of the user's journey's.
 */
public class MyJourneysListFragment extends Fragment {

    public interface OnJourneySelectedListener {
        void onJourneySelected(Journey journey);
        void onCreateJourney();
    }

    private FragmentMyJourneysBinding binding;
    private JourneyAdapter adapter;

    public static MyJourneysListFragment newInstance() {
        return new MyJourneysListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JourneyAdapter(new ArrayList<Journey>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentMyJourneysBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvJourneys.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.rvJourneys.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvJourneys.setAdapter(adapter);

        fetchJourneys();
    }

    private void showInitialLoadProgressBar() {
        binding.pbInitialLoad.setVisibility(View.VISIBLE);
        binding.rvJourneys.setVisibility(View.GONE);
    }

    private void hideInitialLoadProgressBar() {
        binding.pbInitialLoad.setVisibility(View.GONE);
    }

    private void showJourneys(List<Journey> journeys) {
        hideInitialLoadProgressBar();
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

        showInitialLoadProgressBar();

        query.findInBackground(new FindCallback<Journey>() {
            @Override
            public void done(List<Journey> objects, ParseException e) {
                if (e == null) {
                    showJourneys(new ArrayList<Journey>());
                } else {
                    Toast.makeText(getContext(),
                            "Error retrieving journeys", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
