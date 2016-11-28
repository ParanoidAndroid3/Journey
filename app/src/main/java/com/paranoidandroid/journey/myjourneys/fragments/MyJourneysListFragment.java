package com.paranoidandroid.journey.myjourneys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.myjourneys.adapters.JourneyAdapter;
import com.paranoidandroid.journey.support.ui.ItemClickSupport;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Load and display a list of the user's journey's.
 */
public class MyJourneysListFragment extends Fragment implements
        JourneyAdapter.OnItemSelectedListener,
        DeleteConfirmationDialogFragment.OnDeleteListener {
    private static final String TAG = "MyJourneysListFragment";
    private static final int REQUEST_CODE = 300;

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
        binding.rvJourneys.getItemAnimator().setChangeDuration(0);

        ItemClickSupport.addTo(binding.rvJourneys).setOnItemLongClickListener(
                new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                deleteJourneyAtPosition(position);
                return true;
            }
        });

        binding.setListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void deleteJourneyAtPosition(int position) {
        Journey item = adapter.get(position);
        FragmentManager fm = getFragmentManager();
        DeleteConfirmationDialogFragment fragment = DeleteConfirmationDialogFragment
                .newInstance(item.getName(), item.getObjectId());
        fragment.setTargetFragment(this, REQUEST_CODE);
        fragment.show(fm, "delete_confirmation");
    }

    @Override
    public void onDelete(String journeyId) {
        int position = adapter.indexOf(journeyId);
        if (position != -1) {
            Journey journey = adapter.remove(position);
            journey.deleteEventually();
        } else {
            Log.e(TAG, "Tried to delete non-existent Journey(" + journeyId + ")");
        }
    }

    private void showProgressBar() {
        binding.pbInitialLoad.show();
        binding.rvJourneys.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        binding.pbInitialLoad.hide();
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
        ParseQuery<Journey> query = Journey.createQuery(ParseUser.getCurrentUser());

        showProgressBar();
        query.findInBackground(new FindCallback<Journey>() {
            @Override
            public void done(List<Journey> objects, ParseException e) {
                adapter.clear();
                hideProgressBar();

                if (e == null) {
                    showJourneys(objects);
                } else {
                    Log.e(TAG, "Failed to fetch journeys:", e);
                }
            }
        });
    }
}
