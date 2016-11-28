package com.paranoidandroid.journey.legplanner.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.support.ui.DynamicHeightImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BookmarksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Bookmark> mBookmarks;
    private List<Bookmark> mSelectedBookmarks;
    private Context mContext;

    public BookmarksListAdapter(Context context, List<Bookmark> items, List<Bookmark> selectedItems) {
        mBookmarks = items;
        mSelectedBookmarks = selectedItems;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_bookmark, parent, false);
        RecyclerView.ViewHolder viewHolder = new BookmarkViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookmarkViewHolder vh = (BookmarkViewHolder) holder;
        Bookmark b = mBookmarks.get(position);
        if (b != null) {
            vh.photo.setHeightRatio(1);
            Glide.with(mContext)
                    .load(b.getImageUrl())
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_placeholder))
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ic_placeholder))
                    .into(vh.photo);
            vh.name.setText(b.getTitle());
            vh.check.setVisibility(mSelectedBookmarks.contains(b) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mBookmarks.size();
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPhoto) DynamicHeightImageView photo;
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.ivCheck) ImageView check;

        public BookmarkViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
