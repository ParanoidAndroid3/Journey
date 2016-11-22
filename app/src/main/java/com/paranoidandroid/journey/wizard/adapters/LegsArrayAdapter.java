package com.paranoidandroid.journey.wizard.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/21/16.
 */

public class LegsArrayAdapter extends RecyclerView.Adapter<LegsArrayAdapter.ViewHolder> {

    private WizardFragment.OnItemUpdatedListener listener;
    private List<LegItem> legs;
    private Context context;

    public LegsArrayAdapter(Context context, WizardFragment.OnItemUpdatedListener listener) {
        this.context = context;
        this.listener = listener;
        this.legs = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_leg_definition, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LegItem leg = legs.get(position);

        // Set item views based on your views and data model
        holder.tvDestination.setText(leg.getDestination());

        holder.btnDeleteLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClick(position);
            }
        });

        holder.btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalendarClick((Button) view, position, true);
            }
        });

        holder.btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalendarClick((Button) view, position, false);
            }
        });

        Calendar startDate = leg.getStartDate();
        if (startDate == null && position > 0) {
            Calendar previousEndDate = getItem(position - 1).getEndDate();
            if (previousEndDate != null) {
                startDate = Calendar.getInstance();
                startDate.setTime(previousEndDate.getTime());
                startDate.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        assignDate(holder.btnStartDate, startDate);
        assignDate(holder.btnEndDate, leg.getEndDate());

    }

    @Override
    public int getItemCount() {
        return legs.size();
    }

    public void add(LegItem item) {
        legs.add(item);
        updateParent();
    }

    public void remove(LegItem item) {
        int i = legs.indexOf(item);
        legs.remove(item);
        updateParent();
    }

    public void insert(LegItem item, int i) {
        legs.set(i, item);
        updateParent();
    }

    public LegItem getItem(int i) {
        return legs.get(i);
    }

    private Context getContext() {
        return context;
    }

    private void updateParent() {
        notifyDataSetChanged();

        List<LegItem> legs = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++){
            legs.add(getItem(i));
        }

        Map<String, Object> result = new HashMap<>();
        result.put(JourneyBuilder.LEGS_KEY, legs);

        listener.updateJourneyData(result);
    }

    /**
     * This method either shows the selected date on the button or sets the background
     * resource to the calendar icon.
     */
    private void assignDate(Button button, Calendar date) {
        if (date == null) {
            button.setText("");
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        } else {
            String str = new SimpleDateFormat("MM/dd", Locale.US).format(date.getTime());
            button.setText(str);
            button.setBackgroundResource(0);
        }
    }

    private void onDeleteClick(int position) {
        remove(getItem(position));
    }

    /**
     * Presents a large calendar view for user to select travel dates.
     */
    private void onCalendarClick(final Button button, final int position, final boolean isStart) {
        final LegItem item = getItem(position);
        Calendar startDate = item.getStartDate();
        Calendar endDate = item.getEndDate();

        final Calendar myCalendar = Calendar.getInstance();
        if (isStart && startDate != null) {
            myCalendar.setTime(startDate.getTime());
        } else if (!isStart && endDate != null){
            myCalendar.setTime(endDate.getTime());
        }

        if (startDate != null && endDate == null && !isStart) {
            myCalendar.setTime(startDate.getTime());
            myCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                assignDate(button, myCalendar);
                if (isStart) {
                    item.setStartDate(myCalendar);
                } else {
                    item.setEndDate(myCalendar);
                }
                insert(item, position);
            }

        };

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnDeleteLeg;
        TextView tvDestination;
        Button btnStartDate;
        Button btnEndDate;

        public ViewHolder(View view) {
            super(view);
            btnDeleteLeg = (ImageButton) view.findViewById(R.id.btnDeleteLeg);
            tvDestination = (TextView) view.findViewById(R.id.tvDestination);
            btnStartDate = (Button) view.findViewById(R.id.btnStartDate);
            btnEndDate = (Button) view.findViewById(R.id.btnEndDate);
        }
    }
}
