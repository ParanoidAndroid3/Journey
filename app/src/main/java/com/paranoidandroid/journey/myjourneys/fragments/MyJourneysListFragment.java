package com.paranoidandroid.journey.myjourneys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.FragmentMyJourneysBinding;
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

    public interface OnJourneyActionListener {
        void onJourneySelected(Journey journey);
        void onJourneyDeleted(Journey journey);
        void onCreateNewJourney();
    }

    private FragmentMyJourneysBinding binding;
    private JourneyAdapter adapter;
    private OnJourneyActionListener listener;

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
        binding = FragmentMyJourneysBinding.inflate(inflater, container, false);
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
        if (context instanceof OnJourneyActionListener) {
            listener = (OnJourneyActionListener) context;
        } else {
            throw new IllegalArgumentException(
                    "context must implement " + OnJourneyActionListener.class);
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
            showEmptyView(adapter.getItemCount() == 0);
            listener.onJourneyDeleted(journey);
            showUndo(journey);
        } else {
            Log.e(TAG, "Tried to delete non-existent Journey(" + journeyId + ")");
        }
    }

    private void showUndo(final Journey journey) {
        String name = journey.getName();
        String title = getString(R.string.deleted_title, name);
        Snackbar.make(binding.rvJourneys, title, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != DISMISS_EVENT_ACTION) {
                            journey.deleteEventually();
                        }
                    }
                })
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.add(0, journey);
                        binding.rvJourneys.scrollToPosition(0);
                    }
                })
                .show();
    }

    private void showProgressBar() {
        binding.pbInitialLoad.show();
        binding.rvJourneys.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        binding.pbInitialLoad.hide();
        binding.rvJourneys.setVisibility(View.VISIBLE);
    }

    private void showJourneys(List<Journey> journeys) {
        boolean isEmpty = journeys.size() == 0;
        showEmptyView(isEmpty);
        adapter.clear();
        if (!isEmpty) {
            adapter.addAll(journeys);
        }
    }

    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            binding.rlEmptyView.setVisibility(View.VISIBLE);
            binding.rvJourneys.setVisibility(View.GONE);
        } else {
            binding.rlEmptyView.setVisibility(View.GONE);
            binding.rvJourneys.setVisibility(View.VISIBLE);
        }
    }

    protected void fetchJourneys() {
        ParseQuery<Journey> query = Journey.createQuery(ParseUser.getCurrentUser());

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
                    if (objects.size() == 0
                            || hasChanges(adapter.getAll(), objects)) {
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
