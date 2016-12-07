package com.paranoidandroid.journey.journeys.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoidandroid.journey.databinding.FragmentJourneysBinding;
import com.paranoidandroid.journey.journeys.adapters.JourneysAdapter;
import com.paranoidandroid.journey.models.Journey;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for loading and displaying Journey lists
 */
public abstract class JourneysListFragment extends Fragment implements JourneysAdapter.OnItemSelectedListener{

    protected static final String TAG = "JourneysListFragment";

    protected FragmentJourneysBinding binding;
    protected JourneysAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JourneysAdapter(new ArrayList<Journey>());
        adapter.setOnJourneySelectedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentJourneysBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvJourneys.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvJourneys.setAdapter(adapter);
        binding.rvJourneys.getItemAnimator().setChangeDuration(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchJourneys();
    }

    protected abstract ParseQuery<Journey> generateQueryForJourneys();

    protected abstract void showEmptyView(boolean isEmpty);

    protected void showProgressBar() {
        binding.pbInitialLoad.show();
        binding.rvJourneys.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        binding.pbInitialLoad.hide();
        binding.rvJourneys.setVisibility(View.VISIBLE);
    }

    protected void showJourneys(List<Journey> journeys) {
        boolean isEmpty = journeys.size() == 0;
        showEmptyView(isEmpty);
        adapter.clear();
        if (!isEmpty) {
            adapter.addAll(journeys);
        }
    }

    public void fetchJourneys() {
        ParseQuery<Journey> query = generateQueryForJourneys();

        // Only show progress bar if the user has nothing to look at.
        if (adapter.size() == 0) {
            showProgressBar();
        }

        query.findInBackground(new FindCallback<Journey>() {
            @Override
            public void done(List<Journey> objects, ParseException e) {
                hideProgressBar();

                if (e == null) {
                    // Show the new journeys if the list was changed.
                    if (true && hasChanges(adapter.getAll(), objects)) {
                        showJourneys(objects);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch journeys:", e);
                }
            }
        });
    }

    // TODO: this method should be somewhere else.
    private static boolean hasChanges(List<Journey> a, List<Journey> b) {
        if (a.size() != b.size()) {
            return true;
        }

        for (int i = 0; i < a.size(); i++) {
            Journey ai = a.get(i);
            Journey bi = a.get(i);
            if (!ai.getObjectId().equals(bi.getObjectId())) {
                return true;
            }
        }
        return false;
    }
}
