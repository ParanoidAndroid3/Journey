package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.support.GooglePlaceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.paranoidandroid.journey.support.MapUtils.getBoundsZoomLevel;

public class MapViewFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraIdleListener {

    private static final String TAG = "MapViewFragment";

    MapView mMapView;
    GoogleMap mGoogleMap;
    MapEventListener listener;
    List<MarkerInfo> markerInfos = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    // Keep track of marker lat/lngs that are in the process of setting up.
    Set<LatLng> pendingMarkers = new HashSet<>();
    private float density;
    Marker selectedMarker;
    LatLngBounds.Builder builder;
    int selectedPosition = -1;
    boolean zoomed = false;
    //private Bundle mBundle;
    public void setZoomed(boolean zoomed) { this.zoomed = zoomed; }
    public boolean isZoomed() { return this.zoomed; }

    public interface MapEventListener {
        void onLegMarkerPressedAtIndex(int position);
        void onActivityMarkerPressedAtIndex(int position);
        void onCameraHasSettled();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        MapsInitializer.initialize(getActivity());

        density = getContext().getResources().getDisplayMetrics().density;
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    // OnMapReadyCallback implementation

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraIdleListener(this);

        boolean success = mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this.getContext(), R.raw.style_json));
    }

    // Called from parent activity on the zoom out (legs view) case
    // TODO improve marker orientation to avoid overlaps

    public void addMarkersFromLegs(List<Leg> legs, int highlightPosition) {
        markerInfos.clear();
        selectedPosition = highlightPosition;

        for (Leg leg : legs) {
            Destination destination = leg.getDestination();
            LatLng latLng = new LatLng(destination.getLatitude(), destination.getLongitude());
            String imageReference = destination.getGoogleImageReference();
            String image = new GooglePlaceInfo(imageReference, latLng).getImageUrl(200);  // hardcode a small size for now
            markerInfos.add(new MarkerInfo(destination.getCityName(), latLng, image));
        }

        placeMarkersOnMap();
    }

    // Called from parent activity on the zoom in (activities view) case
    // TODO improve marker orientation to avoid overlaps

    public void addMarkersFromActivities(List<Activity> activities, Leg leg) {
        markerInfos.clear();
        selectedPosition = 0; // On resume, ignore selectedPosition

        // No activities for selected leg, just zoom to city center (level 10)
        if (activities.size() == 0) {
            Destination destination = leg.getDestination();
            LatLng latLng = new LatLng(destination.getLatitude(), destination.getLongitude());
            String imageReference = destination.getGoogleImageReference();
            String image = new GooglePlaceInfo(imageReference, latLng).getImageUrl(200); // TODO: hardcode a small size for now
            markerInfos.add(new MarkerInfo(destination.getCityName(), latLng, image));
        } else {
            for (Activity activity : activities) {
                LatLng latLng = new LatLng(activity.getLatitude(), activity.getLongitude());
                String title = activity.getTitle();
                String image = activity.getImageUrl();
                markerInfos.add(new MarkerInfo(title, latLng, image));
            }
        }

        placeMarkersOnMap();
    }

    // Places the markers on map given an array of MarkerInfo's

    private void placeMarkersOnMap() {
        // Create a new array of markers, initialize all to null
        markers.clear();
        for (int i = 0; i < markerInfos.size(); i++)
            markers.add(null);
        // Clear all the map
        mGoogleMap.clear();
        // Nullify selected marker to release its reference
        selectedMarker = null;
        pendingMarkers.clear();

        builder = new LatLngBounds.Builder();
        LatLng prev = null; int index = 0;
        for (MarkerInfo m : markerInfos) {
            IconGenerator iconFactory = new IconGenerator(getContext());
            addIcon(iconFactory, m.city, m.coordinates, m.imageUrl, index);
            if (prev != null) {
                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(prev, m.coordinates)
                        .width(6)
                        .color(ContextCompat.getColor(getContext(), R.color.colorAccent))
                        .geodesic(false));
            }
            prev = m.coordinates; index++;
        }
    }

    // Highlight the selected marker, deselect the previous one

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerPosition = markers.indexOf(marker);

        if (markerPosition < 0) {
            Log.e(TAG, "Couldn't find marker " + marker);
            return true;
        }

        selectMarker(markerPosition);

        // notify listener
        if (listener != null) {
            if (isZoomed()) {
                listener.onActivityMarkerPressedAtIndex(markerPosition);
            } else {
                listener.onLegMarkerPressedAtIndex(markerPosition);
            }
        }

        // Event was handled by our code do not launch default behaviour.
        return true;
    }

    // Called from activity when the view pager changes

    public void changeToMarkerPosition(int markerPosition) {
        if (markerPosition == selectedPosition)
            return;

        selectMarker(markerPosition);
    }

    // Called from to quickly return selected marker coordinates

    public LatLng getSelectedMarkerCoordinates() {
        return selectedMarker.getPosition();
    }

    private void selectMarker(int markerPosition) {
        selectedPosition = markerPosition;
        if (markers.get(selectedPosition) != null) {
            selectedMarker = markers.get(selectedPosition);
            selectedMarker.setZIndex(1); // bring to front in case of overlap
        }
    }

    private void addIcon(final IconGenerator iconFactory, final CharSequence text, final LatLng position, String imageUrl, final int index) {

        LayoutInflater myInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = myInflater.inflate(R.layout.custom_map_marker, null, false);
        final ImageView imageView = (ImageView) activityView.findViewById(R.id.ivPhoto);
        iconFactory.setContentView(activityView);

        pendingMarkers.add(position);

        Glide.with(getContext())
                .load(imageUrl)
                .into((new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (pendingMarkers.contains(position)) {
                            imageView.setImageDrawable(resource);
                            addMarker(getOptions(), position, index);
                            pendingMarkers.remove(position);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        if (pendingMarkers.contains(position)) {
                            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_journey));
                            addMarker(getOptions(), position, index);
                            pendingMarkers.remove(position);
                        }
                    }

                    private MarkerOptions getOptions() {
                        return new MarkerOptions().
                                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                                //alpha((float)0.7).
                                position(position).
                                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                    }
                }));
    }

    private boolean hasAllPinsPlaced() {
        return !markers.contains(null);
    }

    private synchronized void addMarker(MarkerOptions options, LatLng position, int index) {
        if (index >= markers.size()) {
            // We must be trying to update the markers for an index that has since been removed.
            return;
        }

        markers.set(index, mGoogleMap.addMarker(options));
        builder.include(position);
        if (hasAllPinsPlaced()) {
            // HACK!! The map may not be loaded when we request to animate
            // If we get an exception, try again after when we get the OnMapLoaded event
            try {
                animateCamera();
                selectMarker(selectedPosition);
            } catch (IllegalStateException ise) {
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                    @Override
                    public void onMapLoaded() {
                        animateCamera();
                        selectMarker(selectedPosition);
                    }
                });
            }
        }
    }

    private void animateCamera() {
        final LatLngBounds bounds = builder.build();

        CameraPosition cp = new CameraPosition.Builder()
                .tilt(45)
                .target(bounds.getCenter())
                .zoom(getBoundsZoomLevel(bounds, getView().findViewById(R.id.map).getMeasuredWidth(), getView().findViewById(R.id.map).getMeasuredHeight(), density))
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        mGoogleMap.animateCamera(cu);

    }

    // When the camera has settled and all pins are placed, notify listener to hide progress bar

    @Override
    public void onCameraIdle() {
        if (hasAllPinsPlaced() && this.listener != null) {
            this.listener.onCameraHasSettled();
        }
    }

    // Forward fragment lifecycle methods to map view

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
        if (!isZoomed()) {
            // save marker position only when showing legs
            outState.putInt("selected", selectedPosition);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt("selected");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapEventListener){
            this.listener = (MapEventListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // Marker information holder class

    private class MarkerInfo {
        public String city;
        public LatLng coordinates;
        public String imageUrl;

        public MarkerInfo(String city, LatLng coordinates, String imageUrl) {
            this.city = city;
            this.coordinates = coordinates;
            this.imageUrl = imageUrl;
        }
    }
}
