package com.paranoidandroid.journey.legplanner.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        View v1 = inflater.inflate(R.layout.item_activity, parent, false);
        RecyclerView.ViewHolder viewHolder = new ActivityViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ActivityViewHolder vh = (ActivityViewHolder) holder;
        Activity activity = mActivities.get(position);
        if (activity != null) {
            vh.ivCustom.setVisibility(activity.getGoogleId() != null ? View.VISIBLE : View.GONE);
            vh.ivFromBookmarks.setVisibility(activity.getGoogleId() == null ? View.VISIBLE : View.GONE);
            vh.tvName.setText(activity.getTitle());
            vh.tvType.setText(activity.getEventType());
            Glide.with(mContext)
                    .load(activity.getImageUrl())
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_map_marker_placeholder))
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ic_map_marker_placeholder))
                    .into(vh.ivPhoto);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewCompat.setTransitionName(vh.ivPhoto, activity.getImageUrl());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvType) TextView tvType;
        @BindView(R.id.ivCustom) ImageView ivCustom;
        @BindView(R.id.ivFroBookmarks) ImageView ivFromBookmarks;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivPhoto) ImageView ivPhoto;

        public ActivityViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
