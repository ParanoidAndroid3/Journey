package com.paranoidandroid.journey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.paranoidandroid.journey.models.Day;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LegPlannerActivity extends AppCompatActivity {

    Journey mJourney;
    DaysListAdapter mDaysAdapter;
    RecyclerView rvDays;
    ArrayList<Day> mDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leg_planner);

        setupToolbar();
        fetchJourney();
        setupDaysRecyclerView();
    }

    private void setupDaysRecyclerView() {
        rvDays = (RecyclerView) findViewById(R.id.rvDays);
        rvDays.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        rvDays.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(rvDays);
        mDaysAdapter = new DaysListAdapter(this, mDays);
        rvDays.setAdapter(mDaysAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void fetchJourney() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Journey");
        query.include("legs");
        query.include("legs.destination");
        query.include("legs.activities");

        query.getInBackground("ZIvsEb6kxY", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mJourney =  (Journey) object;
                    List<Leg> legs = mJourney.getLegs();
                    populateDaysFromLegs(legs);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateDaysFromLegs(List<Leg> legs) {
        if (legs.size() > 0) {
            List<Day> daysFromLegs = new ArrayList<>();
            AtomicInteger series = new AtomicInteger(0);
            for (Leg leg : legs) {
                daysFromLegs.addAll(getDaysBetween(leg.getStartDate(), leg.endDate(), series, leg.getDestination().getCityName()));
            }
            mDays.addAll(daysFromLegs);
            mDaysAdapter.notifyDataSetChanged();
        }
    }

    private List<Day> getDaysBetween(Date startDate, Date endDate, AtomicInteger series, String city) {
        List<Day> days = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        while (calendar.getTime().before(endDate))
        {
            Date dayDate = calendar.getTime();
            days.add(new Day(series.addAndGet(1), dayDate, city));
            calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

}
