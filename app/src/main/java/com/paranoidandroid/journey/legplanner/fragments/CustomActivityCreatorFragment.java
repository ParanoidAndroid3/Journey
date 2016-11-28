package com.paranoidandroid.journey.legplanner.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

import static com.paranoidandroid.journey.support.MapUtils.toBounds;

public class CustomActivityCreatorFragment extends DialogFragment implements
        PlaceSelectionListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.etName) EditText etName;
    @BindView(R.id.place_photo) ImageView ivPhoto;

    private LatLng coordinates;
    private OnAddCustomActivityListener listener;
    private GooglePlace googlePlace;
    private Date date;
    private String city;
    private Unbinder unbinder;

    public interface OnAddCustomActivityListener {
        void onAddCustomActivity(String title, Date date, GooglePlace place);
    }

    public static CustomActivityCreatorFragment newInstance(LatLng coordinates, Date date, String city) {
        CustomActivityCreatorFragment frag = new CustomActivityCreatorFragment();
        Bundle args = new Bundle();
        args.putParcelable("coordinates", coordinates);
        args.putLong("date", date.getTime());
        args.putString("city", city);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddCustomActivityListener){
            this.listener = (OnAddCustomActivityListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinates = getArguments().getParcelable("coordinates");
        date = new Date(getArguments().getLong("date"));
        city = getArguments().getString("city");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_activity, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        SimpleDateFormat f = new SimpleDateFormat("M/d");
        TextView tvDateCity = (TextView) rootView.findViewById(R.id.tvDateCity);
        tvDateCity.setText(city + " on "+f.format(date));

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Search a Location");
        autocompleteFragment.setBoundsBias(toBounds(coordinates, 10000));

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onPlaceSelected(Place place) {
        tvAddress.setText(place.getAddress());
        GooglePlaceSearchClient.findDetails(place.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                googlePlace = GooglePlace.parsePlaceDetails(response);
                loadPlacePhoto();
            }
        });
    }

    private void loadPlacePhoto() {
        if (googlePlace == null)
            return;
        Glide.with(getContext())
                .load(googlePlace.getImageUrl())
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_marker_placeholder))
                .error(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_marker_placeholder))
                .into(ivPhoto);
    }

    @Override
    public void onError(Status status) {
        Snackbar.make(etName, "Place selection failed: ", Snackbar.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_custom_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (this.listener == null) {
                dismiss();
                return false;
            } else if (etName.getText().toString().isEmpty()) {
                Snackbar.make(etName, "Please enter a name for your activity", Snackbar.LENGTH_SHORT).show();
            } else if (googlePlace == null) {
                Snackbar.make(etName, "Please enter a location", Snackbar.LENGTH_SHORT).show();
            } else {
                this.listener.onAddCustomActivity(etName.getText().toString(), date, googlePlace);
            }
            dismiss();
            return true;
        } else if (id == android.R.id.home) {
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
