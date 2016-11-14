package com.paranoidandroid.journey.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;

import java.util.List;

public class ActivitiesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Activity> mActivities;
    private Context mContext;

    public ActivitiesListAdapter(Context context, List<Activity> items) {
        mActivities = items;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.viewholder_activity, parent, false);
        RecyclerView.ViewHolder viewHolder = new ActivityViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActivityViewHolder vh = (ActivityViewHolder) holder;
        Activity activity = mActivities.get(position);
        if (activity != null) {
            vh.tvName.setText(activity.getTitle());
            vh.tvType.setText(activity.getEventType());
        }
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvType;
        public ActivityViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvType = (TextView) v.findViewById(R.id.tvType);
        }
    }
}
