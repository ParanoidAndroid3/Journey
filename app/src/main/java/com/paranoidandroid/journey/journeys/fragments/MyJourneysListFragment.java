package com.paranoidandroid.journey.journeys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.support.ui.ItemClickSupport;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Load and display a list of the user's journey's.
 */
public class MyJourneysListFragment extends JourneysListFragment implements DeleteConfirmationDialogFragment.OnDeleteListener {

    private static final int REQUEST_CODE = 300;

    public interface OnJourneyActionListener {
        void onJourneySelected(Journey journey);
        void onJourneyDeleted(Journey journey);
        void onCreateNewJourney();
    }

    private OnJourneyActionListener listener;

    public static MyJourneysListFragment newInstance() {
        return new MyJourneysListFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    protected ParseQuery<Journey> generateQueryForJourneys() {
        return Journey.createQuery(ParseUser.getCurrentUser());
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

}
