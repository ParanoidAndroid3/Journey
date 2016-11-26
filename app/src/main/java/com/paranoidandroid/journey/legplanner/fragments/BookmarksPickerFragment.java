package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.adapters.BookmarksListAdapter;
import com.paranoidandroid.journey.models.Bookmark;
import com.paranoidandroid.journey.support.ui.ItemClickSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookmarksPickerFragment extends BottomSheetDialogFragment {
    @BindView(R.id.rvBookmarks) RecyclerView rvBookmarks;
    @BindView(R.id.tvSelect) TextView tvSelect;
    @BindView(R.id.btAdd) Button btAdd;
    @BindView(R.id.btCancel) Button btCancel;
    private Unbinder unbinder;
    private OnBookmarksSelectedListener listener;
    private Date date;
    private String city;

    ArrayList<Bookmark> mBookmarks;
    ArrayList<Bookmark> mSelectedBookmarks;
    BookmarksListAdapter mBookmarksAdapter;

    public interface OnBookmarksSelectedListener {
        void onBookmarksSelected(List<Bookmark> bookmarks);
        List<Bookmark> getBookmarksList();
    }

    public static BookmarksPickerFragment newInstance(Date date, String city) {
        BookmarksPickerFragment frag = new BookmarksPickerFragment();
        Bundle args = new Bundle();
        args.putLong("date", date.getTime());
        args.putString("city", city);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookmarksSelectedListener) {
            this.listener = (OnBookmarksSelectedListener) context;
        } else if (getParentFragment() instanceof OnBookmarksSelectedListener) {
            this.listener = (OnBookmarksSelectedListener) getParentFragment();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement BookmarksPickerFragment.OnBookmarksSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = new Date(getArguments().getLong("date"));
        city = getArguments().getString("city");
        mBookmarks = new ArrayList<>();
        mSelectedBookmarks = new ArrayList<>();
        mBookmarksAdapter = new BookmarksListAdapter(getActivity(), mBookmarks, mSelectedBookmarks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark_picker, container, false);
        unbinder = ButterKnife.bind(this, v);
        SimpleDateFormat f = new SimpleDateFormat("M/d");
        String selectText = "Select bookmarks to add for " + f.format(date) + " in " + city;
        tvSelect.setText(selectText);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupBookmarksList(view);
    }

    private void setupBookmarksList(View view) {
        ItemClickSupport.addTo(rvBookmarks).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int pos, View v) {
                Bookmark selectedBookmark = mBookmarks.get(pos);
                if (mSelectedBookmarks.contains(selectedBookmark)) {
                    mSelectedBookmarks.remove(selectedBookmark);
                } else {
                    mSelectedBookmarks.add(selectedBookmark);
                }
                mBookmarksAdapter.notifyItemChanged(pos);
                showHideAddButton();

            }
        });
        rvBookmarks.setAdapter(mBookmarksAdapter);
        rvBookmarks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        populateBookmarks();
        showHideAddButton();
    }

    private void showHideAddButton() {
        btAdd.setVisibility(mSelectedBookmarks.size() > 0 ? View.VISIBLE : View.GONE);
        btAdd.setText("Add " + mSelectedBookmarks.size() + " bookmark" + (mSelectedBookmarks.size() > 1 ? "s" : ""));
    }

    public void populateBookmarks() {
        if (listener != null) {
            mBookmarks.clear();
            // TODO only add bookmarks that do not exist as activities on the selected day
            mBookmarks.addAll(listener.getBookmarksList());
            mBookmarksAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.btCancel)
    public void cancel(View v) {
        dismiss();
    }

    @OnClick(R.id.btAdd)
    public void add(View v) {
        if (listener != null && mSelectedBookmarks.size() > 0) {
            listener.onBookmarksSelected(mSelectedBookmarks);
        }
        dismiss();
    }

    @Override
    public void onCancel (DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
