package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paranoidandroid.journey.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddCustomActivityFragment extends Fragment {

    private Unbinder unbinder;
    private CustomActivityAddListener listener;
    @BindView(R.id.btAdd) Button btAdd;
    @BindView(R.id.etTitle) EditText etTitle;

    public interface CustomActivityAddListener {
        void onCustomActivityAdded(String title);
    }

    public AddCustomActivityFragment() { }

    public static AddCustomActivityFragment newInstance(String title) {
        AddCustomActivityFragment frag = new AddCustomActivityFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @OnClick(R.id.btAdd)
    public void onAddClicked(View view) {
        if (listener != null) {
            listener.onCustomActivityAdded(etTitle.getText().toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_custom_activity, container, false);
        unbinder = ButterKnife.bind(this, view);
        this.listener = (CustomActivityAddListener) getParentFragment();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle.requestFocus();

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}