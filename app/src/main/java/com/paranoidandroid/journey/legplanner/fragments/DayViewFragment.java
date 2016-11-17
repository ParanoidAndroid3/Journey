package com.paranoidandroid.journey.legplanner.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.adapters.ActivitiesListAdapter;
import com.paranoidandroid.journey.legplanner.adapters.DaysListAdapter;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.ui.Day;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.support.ui.ItemClickSupport;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DayViewFragment extends Fragment implements
        ActivitiesListAdapter.ActivityAdapterClickListener {

    @BindView(R.id.rvDays) RecyclerView rvDays;
    @BindView(R.id.rvActivities) RecyclerView rvActivities;
    @BindView(R.id.tvActivitiesHeader) TextView tvActivitiesHeader;
    private int mSelectedDayIndex;
    private Unbinder unbinder;
    ArrayList<Day> mDays;
    ArrayList<Activity> mActivities;
    DaysListAdapter mDaysAdapter;
    ActivitiesListAdapter mActivitiesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDays = new ArrayList<>();
        mDaysAdapter = new DaysListAdapter(getActivity(), mDays);
        mActivities = new ArrayList<>();
        mActivitiesAdapter = new ActivitiesListAdapter(getActivity(), mActivities);
        mActivitiesAdapter.setActivityAdapterClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupDaysList(view);
        setupActivitiesList(view);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupDaysList(View view) {
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
    }

    private void setupActivitiesList(View view) {
        rvActivities.setAdapter(mActivitiesAdapter);
        rvActivities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        registerForContextMenu(rvActivities);
    }

    private void setSelectedDay(int position) {
        mSelectedDayIndex = position;
        mDaysAdapter.setSelectedIndex(position);
        mDaysAdapter.notifyDataSetChanged();
        mActivities.clear();
        mActivities.addAll(getActivitiesForSelectedDay());
        mActivitiesAdapter.notifyDataSetChanged();
        tvActivitiesHeader.setVisibility(mActivities.size() > 0 ? View.GONE : View.VISIBLE);
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

    // Called from parent Activity when a custom activity is ready for addition

    public void addCustomActivity(String title) {
        // TODO: Add location
        final ParseObject customActivity = ParseObject.create("Activity");
        customActivity.put("title", title);
        customActivity.put("date", getSelectedDay().getDate());
        customActivity.put("eventType", "Custom");
        customActivity.put("geoPoint", getSelectedLeg().getDestination().getGeoPoint());
        customActivity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Leg leg = getSelectedLeg();
                    leg.addActivity((Activity) customActivity);
                    leg.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                setSelectedDay(mSelectedDayIndex);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // ActivityAdapterClickListener implementation

    @Override
    public void onDeleteActivityAtAdapterIndex(int position) {
        if (mActivities.size() > position) {
            Leg leg = getSelectedLeg();
            leg.removeActivity(mActivities.get(position));
            leg.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("", "deleted!!!");
                        setSelectedDay(mSelectedDayIndex);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Helpers

    public Leg getSelectedLeg() {
        return mDays.get(mSelectedDayIndex).getLeg();
    }

    public Day getSelectedDay() {
        return mDays.get(mSelectedDayIndex);
    }

    private List<Day> extractDaysFromLeg(Leg leg, AtomicInteger dayOrder) {
        List<Day> days = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leg.getStartDate());
        while (calendar.getTime().before(leg.getEndDate()))
        {
            Date dayDate = calendar.getTime();
            days.add(new Day(dayOrder.addAndGet(1), dayDate, leg));
            calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

    private List<Activity> getActivitiesForSelectedDay() {
        List<Activity> result = new ArrayList<>();
        if (getSelectedLeg().getActivities() == null) {
            return result;
        }
        for (Activity activity : getSelectedLeg().getActivities()) {
            if (datesOnSameDay(activity.getDate(), getSelectedDay().getDate()))
                result.add(activity);
        }
        return result;
    }

    private boolean datesOnSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
