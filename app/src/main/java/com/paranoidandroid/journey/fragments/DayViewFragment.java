package com.paranoidandroid.journey.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.adapters.ActivitiesListAdapter;
import com.paranoidandroid.journey.adapters.DaysListAdapter;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Day;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DayViewFragment extends Fragment {

    ArrayList<Day> mDays;
    ArrayList<Activity> mActivities;
    DaysListAdapter mDaysAdapter;
    ActivitiesListAdapter mActivitiesAdapter;
    RecyclerView rvDays, rvActivities;
    private int mSelectedDayIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDays = new ArrayList<>();
        mDaysAdapter = new DaysListAdapter(getActivity(), mDays);
        mActivities = new ArrayList<>();
        mActivitiesAdapter = new ActivitiesListAdapter(getActivity(), mActivities);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_view, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Setup Days list

        rvDays = (RecyclerView) view.findViewById(R.id.rvDays);
        ItemClickSupport.addTo(rvDays).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                setSelectedDay(position);
            }
        });
        rvDays.setAdapter(mDaysAdapter);
        rvDays.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvDays.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(rvDays);

        // Setup Activities list

        rvActivities = (RecyclerView) view.findViewById(R.id.rvActivities);
        //mDaysAdapter.setItemsListAdapterClickListener(this);
        rvActivities.setAdapter(mActivitiesAdapter);
        rvActivities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void setSelectedDay(int position) {
        mSelectedDayIndex = position;
        mDaysAdapter.setSelectedIndex(position);
        mDaysAdapter.notifyDataSetChanged();
        mActivities.clear();
        mActivities.addAll(getActivitiesForSelectedDay());
        mActivitiesAdapter.notifyDataSetChanged();
    }

    // Called from parent Activity when legs are available. Populates lists.

    public void populateDaysFromLegs(List<Leg> legs) {
        mDays.clear();
        if (legs.size() > 0) {
            List<Day> allJourneyDays = new ArrayList<>();
            AtomicInteger dayOrder = new AtomicInteger(0);
            for (Leg leg : legs) {
                allJourneyDays.addAll(extractDaysFromLeg(leg, dayOrder));
            }
            mDays.addAll(allJourneyDays);
            setSelectedDay(0);
        }
    }

    public Leg getSelectedLeg() {
        Log.d("aaaa", "selected leg");
        Log.d("aaaa", mDays.get(mSelectedDayIndex).getLeg() + " ");
        return mDays.get(mSelectedDayIndex).getLeg();
    }

    private List<Activity> getActivitiesForSelectedDay() {
        List<Activity> result = new ArrayList<>();
        if (getSelectedLeg().getActivities() == null) {
            return result;
        }
        for (Activity activity : getSelectedLeg().getActivities()) {
            if (activity.getDayOfJourney() == mSelectedDayIndex)
                result.add(activity);
        }
        return result;
    }

    // Return a list of Day objects for all days in a Leg

    private List<Day> extractDaysFromLeg(Leg leg, AtomicInteger dayOrder) {
        List<Day> days = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leg.getStartDate());
        while (calendar.getTime().before(leg.endDate()))
        {
            Date dayDate = calendar.getTime();
            days.add(new Day(dayOrder.addAndGet(1), dayDate, leg));
            calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // TODO: Detach listeners
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.Activity){
            // TODO: Attach listeners
        }
    }
}
