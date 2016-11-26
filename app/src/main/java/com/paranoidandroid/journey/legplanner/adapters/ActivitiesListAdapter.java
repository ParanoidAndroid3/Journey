package com.paranoidandroid.journey.legplanner.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.interfaces.ActivityViewHolderClickListener;
import com.paranoidandroid.journey.models.Activity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivitiesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ActivityViewHolderClickListener {

    private List<Activity> mActivities;
    private Context mContext;
    private ActivityAdapterClickListener listener;

    public interface ActivityAdapterClickListener {
        void onDeleteActivityAtAdapterIndex(int position);
        void onSelectActivityAtAdapterIndex(int position);
    }

    public void setActivityAdapterClickListener(ActivityAdapterClickListener listener) {
        this.listener = listener;
    }

    public ActivitiesListAdapter(Context context, List<Activity> items) {
        mActivities = items;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_activity, parent, false);
        RecyclerView.ViewHolder viewHolder = new ActivityViewHolder(v1, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ActivityViewHolder vh = (ActivityViewHolder) holder;
        Activity activity = mActivities.get(position);
        if (activity != null) {
            vh.tvName.setText(activity.getTitle());
            vh.tvType.setText(activity.getEventType());
            Glide.with(mContext)
                    .load(activity.getImageUrl())
                    .placeholder(R.drawable.ic_map_marker_placeholder)
                    .error(R.drawable.ic_map_marker_placeholder)
                    .into(vh.ivPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    // ActivityViewHolderClickListener implementation

    @Override
    public void onDeletePressedAtIndex(int position) {
        if (listener != null) {
            listener.onDeleteActivityAtAdapterIndex(position);
        }
    }

    @Override
    public void onActivitySelectedAtIndex(int position) {
        if (listener != null) {
            listener.onSelectActivityAtAdapterIndex(position);
        }
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder implements
            View.OnLongClickListener, View.OnClickListener {

        @BindView(R.id.tvType) TextView tvType;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivPhoto) ImageView ivPhoto;

        ActivityViewHolderClickListener mListener;

        public ActivityViewHolder(View v, ActivityViewHolderClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public boolean onLongClick(View view) {
            new AlertDialog.Builder(tvName.getContext())
                    .setTitle("Delete Activity")
                    .setMessage("Do you want to delete this activity?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    mListener.onDeletePressedAtIndex(position);
                                }
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setIcon(R.drawable.ic_journey)
                    .show();
            return true;
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onActivitySelectedAtIndex(position);
                }
            }
        }
    }
}
