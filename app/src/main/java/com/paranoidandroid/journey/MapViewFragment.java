package com.paranoidandroid.journey;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.paranoidandroid.journey.models.Leg;

import java.text.SimpleDateFormat;
import java.util.List;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    MapView mMapView;
    GoogleMap mGoogleMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.Activity){
            // TODO: Attach listeners
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // TODO: Detach listeners
    }

    // OnMapReadyCallback implementation

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        boolean success = mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this.getContext(), R.raw.style_json));
    }

    // Called from parent Activity when legs are available. Populates markers.

    public void addMarkersFromLegs(List<Leg> legs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        boolean isFirst = true;
        LatLng prevLatLng = null;
        int index = 1;
        for (Leg leg : legs) {
            //BitmapDescriptor defaultMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_marker));
            BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker();
            LatLng currentLatLng = new LatLng(leg.getDestination().getLatitude(), leg.getDestination().getLongitude());
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Leg " + index++ + " - " + leg.getDestination().getCityName())
                    .snippet(format.format(leg.getStartDate()) + " - " + format.format(leg.endDate()))

                    .icon(defaultMarker));
            if (isFirst) { marker.showInfoWindow(); isFirst = false; }
            if (prevLatLng != null) {
                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(prevLatLng, currentLatLng)
                        .width(5)
                        .color(Color.GRAY)
                        .geodesic(true));
            }
            prevLatLng = currentLatLng;
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 240; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
