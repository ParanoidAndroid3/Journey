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
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/21/16.
 */

public class LegsArrayAdapter extends RecyclerView.Adapter<LegsArrayAdapter.ViewHolder> {

    private WizardFragment.OnItemUpdatedListener listener;
    private List<Leg> legs;
    private Context context;
    private boolean forceUpdate;

    public LegsArrayAdapter(Context context, List<Leg> legs, WizardFragment.OnItemUpdatedListener listener, boolean forcedUpdate) {
        this.context = context;
        this.listener = listener;
        this.legs = legs;
        this.forceUpdate = forcedUpdate;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Leg leg = legs.get(position);

        // Set item views based on your views and data model
        holder.tvDestination.setText(leg.getDestination().getDisplayName());

        holder.btnDeleteLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalendarClick((Button) view, holder.getAdapterPosition(), true);
            }
        });

        holder.btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalendarClick((Button) view, holder.getAdapterPosition(), false);
            }
        });

        Calendar startDate = getCal(leg.getStartDate());

        if (startDate == null && position > 0) {
            Date previousEndDate = getItem(position - 1).getEndDate();
            if (previousEndDate != null) {
                startDate = Calendar.getInstance();
                startDate.setTime(previousEndDate);
                startDate.add(Calendar.DAY_OF_YEAR, 1);
                leg.setStartDate(startDate.getTime());
                legs.set(position, leg);
            }
        }

        assignDate(holder.btnStartDate, startDate);
        assignDate(holder.btnEndDate, leg.getEndDate());

    }

    @Override
    public int getItemCount() {
        return legs.size();
    }

    public void add(Leg item) {
        legs.add(item);
        if (forceUpdate) {
            listener.getJourney().addLeg(item);
        }
        updateParent();
    }

    public void remove(Leg item) {
        legs.remove(item);
        if (forceUpdate) {
            Destination destination = item.getDestination();
            listener.getJourney().removeLeg(item);
            destination.deleteInBackground();
            item.deleteInBackground();
        }
        updateParent();
    }

    public void insert(Leg item, int i) {
        legs.set(i, item);
        updateParent();
    }

    public Leg getItem(int i) {
        return legs.get(i);
    }

    private Context getContext() {
        return context;
    }

    private void updateParent() {
        notifyDataSetChanged();

        List<Leg> legs = new ArrayList<>();
        boolean enableFab = getItemCount() > 0;
        for (int i = 0; i < getItemCount(); i++){
            Leg leg = getItem(i);
            if (leg.getStartDate() == null || leg.getEndDate() == null) {
                enableFab = false;
            }
            legs.add(getItem(i));
        }

        listener.enableFab(enableFab);

        Map<String, Object> result = new HashMap<>();
        result.put(JourneyBuilder.LEGS_KEY, legs);

        listener.updateJourneyData(result);
    }

    private void assignDate(Button button, Date date) {
        assignDate(button, getCal(date));
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
            button.setTextColor(getContext().getResources().getColor(R.color.colorWizardName));
            button.setBackgroundResource(0);
        }
    }

    private void onDeleteClick(int position) {
        remove(getItem(position));
        if (getItemCount() == 0) {
            listener.enableFab(false);
        }
    }

    /**
     * Presents a large calendar view for user to select travel dates.
     */
    private void onCalendarClick(final Button button, final int position, final boolean isStart) {
        final Leg item = getItem(position);
        Calendar startDate = getCal(item.getStartDate());
        Calendar endDate = getCal(item.getEndDate());

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
                    item.setStartDate(myCalendar.getTime());
                } else {
                    item.setEndDate(myCalendar.getTime());
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

    private Calendar getCal(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
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
