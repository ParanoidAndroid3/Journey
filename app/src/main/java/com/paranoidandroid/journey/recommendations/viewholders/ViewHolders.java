package com.paranoidandroid.journey.recommendations.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationViewHolderClickListener;
import com.paranoidandroid.journey.support.ui.DynamicHeightImageView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolders {

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder implements
            SparkEventListener {
        @BindView(R.id.tvName) public TextView name;
        @BindView(R.id.ivPhoto) public DynamicHeightImageView photo;
        @BindView(R.id.tvDistance) public TextView distance;
        @BindView(R.id.ibSave) public SparkButton save;
        public RecommendationViewHolderClickListener listener;

        public RecommendationViewHolder(View v, RecommendationViewHolderClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            this.listener = listener;
            save.setEventListener(this);
        }

        public RecommendationViewHolder(View v) {
            this(v, null);
        }

        @Override
        public void onEvent(ImageView button, boolean buttonState) {
            onSaveClicked(button);
        }

        public void onSaveClicked(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onAddBookmarkClickedAt(position);
                }
            }
        }
    }
}
