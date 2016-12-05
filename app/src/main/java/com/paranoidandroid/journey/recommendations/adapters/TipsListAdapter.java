package com.paranoidandroid.journey.recommendations.adapters;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.models.ui.Tip;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationViewHolderClickListener;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.support.MapUtils;
import com.paranoidandroid.journey.support.ui.DynamicHeightImageView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.paranoidandroid.journey.recommendations.viewholders.ViewHolders.RecommendationViewHolder;

public class TipsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tip> items;
    private Context context;
    private SimpleDateFormat f;

    public TipsListAdapter(Context context, List<Tip> items) {
        this.items = items;
        this.context = context;
        this.f = new SimpleDateFormat("M/d/yyyy");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);;
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        if (items.get(position) != null) {
            Tip tip = items.get(position);
            vh.date.setText(f.format(tip.getCreatedAt()));
            vh.name.setText(tip.getUserName() == null || tip.getUserName().isEmpty() ? "Anonymous" : tip.getUserName());
            vh.tipText.setText(tip.getText());
            Glide.with(context).load(tip.getUserPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_tip_user))
                    .error(ContextCompat.getDrawable(context, R.drawable.ic_tip_user))
                    .into(vh.photo);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) public TextView name;
        @BindView(R.id.ivPhoto) public ImageView photo;
        @BindView(R.id.tvTipText) public TextView tipText;
        @BindView(R.id.tvDate) public TextView date;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}