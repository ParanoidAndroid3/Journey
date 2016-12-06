package com.paranoidandroid.journey.legplanner.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Activity;
import com.paranoidandroid.journey.models.Leg;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraIdleListener {

    private static final String TAG = "MapViewFragment";

    MapView mMapView;
    GoogleMap mGoogleMap;
    MapEventListener listener;
    List<MarkerInfo> markerInfos;
    List<Marker> markers;
    Marker selectedMarker;
    int selectedPosition = 0;
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
    // Basic login to avoid overlapping markers TODO improve

    public void addMarkersFromLegs(List<Leg> legs, int highlightPosition) {
        markerInfos = new ArrayList<>();
        selectedPosition = highlightPosition;

        LatLng prev = null;
        int index = 0;
        for (Leg leg : legs) {
            LatLng latLng = new LatLng(leg.getDestination().getLatitude(), leg.getDestination().getLongitude());
            if (index == selectedPosition) {
                markerInfos.add(new MarkerInfo(90, -90, IconGenerator.STYLE_BLUE, leg.getDestination().getCityName(), latLng));
            } else if (prev != null && prev.longitude < latLng.longitude) {
                markerInfos.add(new MarkerInfo(90, -90, IconGenerator.STYLE_DEFAULT, leg.getDestination().getCityName(), latLng));
            } else {
                markerInfos.add(new MarkerInfo(-90, 90, IconGenerator.STYLE_DEFAULT, leg.getDestination().getCityName(), latLng));
            }
            prev = latLng;
            index++;
        }
        placeMarkersOnMap();
    }

    // Called from parent activity on the zoom in (activities view) case
    // Basic login to avoid overlapping markers TODO improve

    public void addMarkersFromActivities(List<Activity> activities, Leg leg) {
        markerInfos = new ArrayList<>();
        selectedPosition = 0; // On resume, ignore selectedPosition

        // No activities for selected leg, just zoom to city center (level 10)
        if (activities.size() == 0) {
            LatLng latLng = new LatLng(leg.getDestination().getLatitude(), leg.getDestination().getLongitude());
            markerInfos.add(new MarkerInfo(90, -90, IconGenerator.STYLE_BLUE, leg.getDestination().getCityName(), latLng));
            placeMarkersOnMap();
            return;
        }

        LatLng prev = null;
        int index = 0;
        for (Activity activity : activities) {
            LatLng latLng = new LatLng(activity.getLatitude(), activity.getLongitude());
            String title = activity.getTitle().length() > 10 ? activity.getTitle().substring(0,10) + "..." : activity.getTitle();
            if (index == selectedPosition) {
                markerInfos.add(new MarkerInfo(90, -90, IconGenerator.STYLE_BLUE, title, latLng));
            } else if (prev != null && prev.longitude < latLng.longitude) {
                markerInfos.add(new MarkerInfo(90, -90, IconGenerator.STYLE_DEFAULT, title, latLng));
            } else {
                markerInfos.add(new MarkerInfo(-90, 90, IconGenerator.STYLE_DEFAULT, title, latLng));
            }
            prev = latLng;
            index++;
        }
        placeMarkersOnMap();
    }

    // Places the markers on map given an array of MarkerInfo's

    private void placeMarkersOnMap() {
        mGoogleMap.clear();
        markers = new ArrayList<>();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng prev = null;
        for (MarkerInfo m : markerInfos) {
            IconGenerator iconFactory = new IconGenerator(getContext());
            iconFactory.setRotation(m.rotation);
            iconFactory.setStyle(m.style);
            iconFactory.setContentRotation(m.contentRotation);
            addIcon(iconFactory, m.city, m.coordinates);
            builder.include(m.coordinates);
            if (prev != null) {
                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(prev, m.coordinates)
                        .width(4)
                        .color(Color.BLACK)
                        .geodesic(false));
            }
            prev = m.coordinates;
        }

        final LatLngBounds bounds = builder.build();
        selectMarker(selectedPosition);

        // HACK!! The map may not be loaded when we request to animate
        // If we get an exception, try again after when we get the OnMapLoaded event
        try {
            mGoogleMap.animateCamera(markers.size() == 1 ?
                    CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10) : // city zoom level
                    CameraUpdateFactory.newLatLngBounds(bounds, 300));          // custom padding TODO improve
        } catch (IllegalStateException ise) {
            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {
                    mGoogleMap.animateCamera(markers.size() == 1 ?
                            CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10) : // city zoom level
                            CameraUpdateFactory.newLatLngBounds(bounds, 300));          // custom padding TODO improve
                }
            });
        }
    }

    // Highlight the selected marker, deselect the previous one

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerPosition = markers.indexOf(marker);
        if (markerPosition == selectedPosition)
            return true;

        // recreate previously selected marker
        recreateMarker(selectedMarker, selectedPosition, false);

        // recreate newly selected marker
        recreateMarker(marker, markerPosition, true);

        // notify listener
        if (listener != null) {
            if (isZoomed()) listener.onActivityMarkerPressedAtIndex(markerPosition);
            else listener.onLegMarkerPressedAtIndex(markerPosition);
        }

        selectMarker(markerPosition);

        // Event was handled by our code do not launch default behaviour.
        return true;
    }

    // Called from activity when the view pager changes

    public void changeToMarkerPosition(int markerPosition) {
        if (markerPosition == selectedPosition)
            return;

        // recreate previously selected marker
        recreateMarker(selectedMarker, selectedPosition, false);

        // recreate newly selected marker
        recreateMarker(markers.get(markerPosition), markerPosition, true);

        selectMarker(markerPosition);
    }

    // Called from to quickly return selected marker coordinates

    public LatLng getSelectedMarkerCoordinates() {
        return selectedMarker.getPosition();
    }

    // Remove and recreate marker taking into account selection

    private void recreateMarker(Marker marker, int markerPosition, boolean selected) {
        markers.remove(markerPosition);
        marker.remove();
        IconGenerator iconFactory = new IconGenerator(getContext());
        iconFactory.setRotation(markerInfos.get(markerPosition).rotation);
        iconFactory.setContentRotation(markerInfos.get(markerPosition).contentRotation);
        if (selected)
            iconFactory.setStyle(IconGenerator.STYLE_BLUE);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(markerInfos.get(markerPosition).city))).
                position(markerInfos.get(markerPosition).coordinates).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        markers.add(markerPosition, mGoogleMap.addMarker(markerOptions));
    }

    private void selectMarker(int markerPosition) {
        selectedPosition = markerPosition;
        selectedMarker = markers.get(selectedPosition);
        selectedMarker.setZIndex(1); // bring to front in case of overlap
    }

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        markers.add(mGoogleMap.addMarker(markerOptions));
    }

    // When the camera has settled, notify listener to hide progress bar

    @Override
    public void onCameraIdle() {
        if (this.listener != null) {
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
        public int rotation;
        public int contentRotation;
        public int style = IconGenerator.STYLE_DEFAULT;
        public String city;
        public LatLng coordinates;

        public MarkerInfo(int rotation, int contentRotation, int style, String city, LatLng coordinates) {
            this.rotation = rotation;
            this.contentRotation = contentRotation;
            this.style = style;
            this.city = city;
            this.coordinates = coordinates;
        }
    }
}
