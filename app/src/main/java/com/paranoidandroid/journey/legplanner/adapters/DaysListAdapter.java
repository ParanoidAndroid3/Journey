package com.paranoidandroid.journey.legplanner.adapters;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.Day;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.tvDay) TextView tvDay;
        @BindView(R.id.tvCity) TextView tvCity;

        public DayViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
