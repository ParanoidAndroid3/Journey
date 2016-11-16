package com.paranoidandroid.journey.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FoursquareVenueSearchClient {
    private static final String BASE_URL = "https://api.foursquare.com/v2/venues/explore";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void search(double latitude, double longitude, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("ll", latitude + "," + longitude);
        params.put("radius", 5000);
        params.put("intent", "browse");
        params.put("venuePhotos", 1);
        params.put("v", 20161001);
        params.put("client_id", "K5BE5J2BYC5I00EK4Y3ITM4E33JHKI5YAWJFSUTV3SAMDHCS");
        params.put("client_secret", "RMV4QC42VKAWVGCXA0D54ITCPQJR42LK51XBHJCNDOJGPRNC");
        System.out.println(params);
        client.get(BASE_URL, params, responseHandler);
    }
}