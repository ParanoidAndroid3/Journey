package com.paranoidandroid.journey.legplanner.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.support.MapUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

import static com.paranoidandroid.journey.support.MapUtils.toBounds;

public class CustomActivityCreatorFragment extends DialogFragment implements
        PlaceSelectionListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.etName) EditText etName;
    @BindView(R.id.place_photo) ImageView ivPhoto;
    @BindView(R.id.dismiss_button) ImageButton btDismiss;
    @BindView(R.id.save_button) Button btSave;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;

    private LatLng coordinates;
    private OnAddCustomActivityListener listener;
    private GooglePlace googlePlace;
    private Date date;
    private String city, imageUrl, eventType, title;
    private Unbinder unbinder;
    private PlaceAutocompleteFragment autocompleteFragment;
    Context mContext;

    public interface OnAddCustomActivityListener {
        void onAddCustomActivity(String title, Date date, GooglePlace place);
        void onUpdateCustomActivity(String title, String eventType, LatLng coordinates, Date date, String city);
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

    public static CustomActivityCreatorFragment newInstance(String title, String eventType, String imageUrl, LatLng coordinates, Date date, String city) {
        CustomActivityCreatorFragment frag = new CustomActivityCreatorFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("eventType", eventType);
        args.putString("imageUrl", imageUrl);
        args.putParcelable("coordinates", coordinates);
        args.putLong("date", date.getTime());
        args.putString("city", city);
        frag.setArguments(args);
        return frag;
    }

    // For compatibility we should include the deprecated method too
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnAddCustomActivityListener){
            this.listener = (OnAddCustomActivityListener) context;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddCustomActivityListener){
            this.listener = (OnAddCustomActivityListener) context;
        }
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinates = getArguments().getParcelable("coordinates");
        date = new Date(getArguments().getLong("date"));
        city = getArguments().getString("city");
        eventType = getArguments().getString("eventType");
        imageUrl = getArguments().getString("imageUrl");
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_activity, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        SimpleDateFormat f = new SimpleDateFormat("M/d");
        TextView tvDateCity = (TextView) rootView.findViewById(R.id.tvDateCity);
        tvDateCity.setText(city + " on "+f.format(date));

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint(eventType == null ? "Search a Location" : eventType);
        autocompleteFragment.setBoundsBias(toBounds(coordinates, 10000));

        if (title != null) {
            etName.setText(title);
            //TODO tvAddress.setText(MapUtils.haversine(coordinates.latitude, coordinates.longitude, ));
        }

        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(ivPhoto);
        }

        return rootView;
    }

    @OnClick(R.id.save_button)
    public void onSaveClicked(View v) {
        if (this.listener == null) {
            dismiss();
            return;
        } else if (etName.getText().toString().isEmpty()) {
            Snackbar.make(etName, "Please enter a name for your activity", Snackbar.LENGTH_SHORT).show();
        } else if (googlePlace == null) {
            if (title != null) {
                this.listener.onUpdateCustomActivity(etName.getText().toString(), tvAddress.getText().toString(), coordinates, date, city);
                dismiss();
            }
            else
                Snackbar.make(etName, "Please enter a location", Snackbar.LENGTH_SHORT).show();
        } else {
            if (title != null)
                this.listener.onUpdateCustomActivity(etName.getText().toString(), tvAddress.getText().toString(), coordinates, date, city);
            else
                this.listener.onAddCustomActivity(etName.getText().toString(), date, googlePlace);

            dismiss();
        }
    }

    @OnClick(R.id.dismiss_button)
    public void onDismissClicked(View v) {
        dismiss();
    }

    @Override
    public void onPlaceSelected(Place place) {
        tvAddress.setText(place.getAddress());
        pbLoading.setVisibility(View.VISIBLE);
        GooglePlaceSearchClient.findDetails(place.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                googlePlace = GooglePlace.parsePlaceDetails(response);
                loadPlacePhoto();
            }
        });
    }

    private void loadPlacePhoto() {
        if (googlePlace == null) {
            pbLoading.setVisibility(View.GONE);
            return;
        }

        Glide.with(this)
                .load(googlePlace.getImageUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pbLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivPhoto);
    }

    @Override
    public void onError(Status status) {
        Snackbar.make(etName, "Place selection failed", Snackbar.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.remove(autocompleteFragment);
        ft.commit();
    }
}
