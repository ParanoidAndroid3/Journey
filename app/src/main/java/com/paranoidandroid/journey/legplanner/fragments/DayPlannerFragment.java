package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.ui.Day;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.support.ui.SmartFragmentStatePagerAdapter;
import com.parse.ParseException;
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

import static com.paranoidandroid.journey.support.DateFormattingUtils.datesOnSameDay;

public class DayPlannerFragment extends Fragment implements
        DayActivitiesFragment.OnActivitySelectedListener,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager) ViewPager viewpager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private DayPlannerListener listener;
    private int mSelectedDayIndex;
    private ArrayList<Day> mDays;
    private Unbinder unbinder;

    public interface DayPlannerListener {
        void onLegIndexChanged(int legOrder);
        void onDayChangedOnSameLeg();
        void onActivityRemovedAtIndex(int activityIndex);
    }

    // Called from Planner Activity when a leg marker is pressed

    public void setSelectedLegPosition(int position) {
        // search for the first day of this leg
        for (Day day : mDays) {
            if (day.getLegOrder() == position) {
                mSelectedDayIndex = day.getSeries();
                viewpager.setCurrentItem(day.getSeries() - 1);
                return;
            }
        }
    }

    // Called from Planner Activity when an activity marker is pressed

    public void scrollToActivityAtIndex(int position) {
        DayActivitiesFragment frag = (DayActivitiesFragment) adapterViewPager.getRegisteredFragment(mSelectedDayIndex);
        frag.scrollToActivityPosition(position);
    }

    public void refreshCurrentPage() {
        DayActivitiesFragment frag = (DayActivitiesFragment) adapterViewPager.getRegisteredFragment(mSelectedDayIndex);
        frag.refreshList();
    }

    // Called from Planner Activity when legs are fetched
    // Populates view pager and tab layout

    public void populateDaysFromLegs(List<Leg> legs) {
        if (legs.size() > 0) {
            List<Day> allJourneyDays = new ArrayList<>();
            AtomicInteger dayOrder = new AtomicInteger(0);
            int legOrder = 0;
            for (Leg leg : legs) {
                allJourneyDays.addAll(extractDaysFromLeg(leg, dayOrder, legOrder++));
            }
            showDays(allJourneyDays);
        }
    }

    private void showDays(List<Day> days) {
        mDays.clear();
        mDays.addAll(days);
        mSelectedDayIndex = 0;
        adapterViewPager = new DaysPagerAdapter(getChildFragmentManager(), getContext(), days);
        viewpager.setAdapter(adapterViewPager);
        viewpager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewpager);
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapterViewPager.getTabView(i));
        }
    }

    // ActivitiesListAdapter's ActivityAdapterClickListener implementation

    @Override
    public void onDeleteActivityRequested(Activity activity) {
        Leg leg = getSelectedLeg();
        final int activityIndex = leg.getActivities().indexOf(activity);
        leg.removeActivity(activity);
        leg.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    refreshCurrentPage();
                    if (listener != null) {
                        listener.onActivityRemovedAtIndex(activityIndex);
                    }
                    // TODO: Inform the activity to remove marker if zoomed in
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Helpers

    public Leg getSelectedLeg() {
        return mDays.get(mSelectedDayIndex).getLeg();
    }

    public int getSelectedLegOrder() {
        return mDays.get(mSelectedDayIndex).getLegOrder();
    }

    public Day getSelectedDay() {
        return mDays.get(mSelectedDayIndex);
    }

    private List<Day> extractDaysFromLeg(Leg leg, AtomicInteger dayOrder, int legOrder) {
        List<Day> days = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leg.getStartDate());
        while (calendar.getTime().before(leg.getEndDate()) || calendar.getTime().equals(leg.getEndDate()))
        {
            Date dayDate = calendar.getTime();
            days.add(new Day(dayOrder.addAndGet(1), dayDate, leg, legOrder));
            calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

    public List<Activity> getActivitiesForSelectedDay() {
        final List<Activity> result = new ArrayList<>();
        if (getSelectedLeg().getActivities() == null) {
            return result;
        }
        try {
            // Need to call this synchronously to avoid 'Object not found' error
            Activity.fetchAllIfNeeded(getSelectedLeg().getActivities());
            for (Activity activity : getSelectedLeg().getActivities()) {
                    Date activityDate = activity.getDate("date");
                    if (activityDate != null && datesOnSameDay(activityDate, getSelectedDay().getDate()))
                        result.add(activity);

            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected", mSelectedDayIndex);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mSelectedDayIndex = savedInstanceState.getInt("selected");
        }
    }

    @Override
    public void onActivitySelected(Activity activity) {
        // TODO: Show detail view
    }

    @Override
    public List<Activity> getActivitiesListForDay(int dayOrder) {
        Leg leg = mDays.get(dayOrder).getLeg();
        final List<Activity> result = new ArrayList<>();
        if (leg.getActivities() == null) {
            return result;
        }
        try {
            // Need to call this synchronously to avoid 'Object not found' error
            Activity.fetchAllIfNeeded(leg.getActivities());
            for (Activity activity : leg.getActivities()) {
                Date activityDate = activity.getDate("date");
                if (activityDate != null && datesOnSameDay(activityDate, mDays.get(dayOrder).getDate()))
                    result.add(activity);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    // OnPageChangeListener implementation

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        // Inform the activity to change the selected leg
        if (listener != null) {
            // Leg has changed
            if (mDays.get(position).getLegOrder() != mDays.get(mSelectedDayIndex).getLegOrder()) {
                listener.onLegIndexChanged(mDays.get(position).getLegOrder());
                mSelectedDayIndex = position;
            }
            // Same leg, different day
            else {
                mSelectedDayIndex = position;
                listener.onDayChangedOnSameLeg();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    // Pager Adapter for the days view pager

    public static class DaysPagerAdapter extends SmartFragmentStatePagerAdapter {
        List<Day> days;
        private Context context;

        public DaysPagerAdapter(FragmentManager fragmentManager,  Context c, List<Day> days) {
            super(fragmentManager);
            this.days = days;
            context = c;
        }

        @Override
        public int getCount() { return days.size(); }

        @Override
        public Fragment getItem(int position) { return DayActivitiesFragment.newInstance(position); }

        @Override
        public CharSequence getPageTitle(int position) {
            return ""; // we are using custom view
        }

        @Override
        public View getTabView(int i) {
            View v = LayoutInflater.from(context).inflate(R.layout.custom_day_tab, null);
            TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvDate.setText(days.get(i).getFormattedDate());
            TextView tvCity = (TextView) v.findViewById(R.id.tvCity);
            tvCity.setText(days.get(i).getCity());
            return v;
        }
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDays = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_planner, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {}

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DayPlannerListener){
            this.listener = (DayPlannerListener) context;
        }
    }
}