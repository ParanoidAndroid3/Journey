package com.paranoidandroid.journey.legplanner.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.paranoidandroid.journey.legplanner.interfaces.ActivityViewHolderClickListener;
import com.paranoidandroid.journey.R;
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
        View v1 = inflater.inflate(R.layout.viewholder_activity, parent, false);
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

    static class ActivityViewHolder extends RecyclerView.ViewHolder implements
            PopupMenu.OnMenuItemClickListener {

        @BindView(R.id.tvType) TextView tvType;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ibMenu) ImageButton ibMenu;
        ActivityViewHolderClickListener mListener;

        public ActivityViewHolder(View v, ActivityViewHolderClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            mListener = listener;
        }

        @OnClick(R.id.ibMenu)
        public void onMenuClicked(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.menu_activity, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onDeletePressedAtIndex(position);
                }
            }
            return true;
        }
    }
}
