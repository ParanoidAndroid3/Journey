package com.paranoidandroid.journey.adapters;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Day;

import java.util.List;

public class DaysListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Day> mDays;
    private Context mContext;
    private int selectedIndex;

    public DaysListAdapter(Context context, List<Day> items) {
        mDays = items;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.viewholder_day, parent, false);
        RecyclerView.ViewHolder viewHolder = new DayViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DayViewHolder vh = (DayViewHolder) holder;
        Day day = mDays.get(position);
        if (day != null) {
            vh.tvDate.setText(day.getFormattedDate());
            vh.tvDay.setText(day.getSeriesString());
            TextViewCompat.setTextAppearance(vh.tvDay, position == selectedIndex ? R.style.SelectedDayText :R.style.DayText );
            vh.tvCity.setText(day.getCity());
        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvDay, tvCity;
        public DayViewHolder(View v) {
            super(v);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvDay = (TextView) v.findViewById(R.id.tvDay);
            tvCity = (TextView) v.findViewById(R.id.tvCity);
        }
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
