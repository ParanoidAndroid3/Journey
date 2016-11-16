package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paranoidandroid.journey.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddActivityFragment extends BottomSheetDialogFragment implements
        AddCustomActivityFragment.CustomActivityAddListener {

    @BindView(R.id.btAddCustom) Button btAddCustom;
    @BindView(R.id.btAddSuggestion) Button btAddSuggestion;
    private Unbinder unbinder;
    private AddActivityListener listener;

    public interface AddActivityListener {
        void onCustomActivityAdded(String title);
        void onRecommendationActivityClicked();
    }

    public static AddActivityFragment newInstance() {
        AddActivityFragment frag = new AddActivityFragment();;
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.Activity){
            this.listener = (AddActivityListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_activity, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    // CustomActivityAddListener implementation

    @Override
    public void onCustomActivityAdded(String title) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(getChildFragmentManager().findFragmentByTag("custom")).commit();
        if (listener != null) {
            listener.onCustomActivityAdded(title);
            dismiss();
        }
    }

    @OnClick(R.id.btAddCustom)
    public void onAddCustomClicked(View view) {
        if (getChildFragmentManager().findFragmentByTag("custom") != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(getChildFragmentManager().findFragmentByTag("custom")).commit();
        } else {
            Fragment childFragment = new AddCustomActivityFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.child_fragment_container, childFragment, "custom").commit();
        }
    }

    @OnClick(R.id.btAddSuggestion)
    public void onAddSuggestionClicked(View view) {
        if (listener != null) {
            listener.onRecommendationActivityClicked();
            dismiss();
        }
    }

    @Override
    public void onCancel (DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
