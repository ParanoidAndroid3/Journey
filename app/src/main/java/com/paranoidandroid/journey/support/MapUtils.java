package com.paranoidandroid.journey.support;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

public class MapUtils {

    // Find the distance of 2 latlngs using math

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8; // In kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    // Create bounds using a latlng center using math

    public static LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    // Find the optimal zoom level from bounds

    private static final double LN2 = 0.6931471805599453;
    private static final int WORLD_PX_HEIGHT = 256;
    private static final int WORLD_PX_WIDTH = 256;
    private static final int ZOOM_MAX = 15;

    public static int getBoundsZoomLevel(LatLngBounds bounds, int mapWidthPx, int mapHeightPx, float density){

        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, (int) (WORLD_PX_HEIGHT * density), latFraction);
        double lngZoom = zoom(mapWidthPx, (int) (WORLD_PX_WIDTH * density), lngFraction);

        int result = Math.min((int)latZoom, (int)lngZoom);
        return Math.min(result, ZOOM_MAX);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(int mapPx, int worldPx, double fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2);
    }
}
