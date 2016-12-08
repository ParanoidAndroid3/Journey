package com.paranoidandroid.journey.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FoursquareVenueSearchClient {
    private static final String BASE_URL = "https://api.foursquare.com/v2/venues/explore";
    private static final String DETAILS_URL = "https://api.foursquare.com/v2/venues/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void search(double latitude, double longitude, String categoryId, int offset, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("ll", latitude + "," + longitude);
        params.put("radius", 5000);
        params.put("intent", "browse");
        params.put("venuePhotos", 1);
        params.put("categoryId", categoryId);
        if (offset > 0)
            params.put("offset", offset);
        params.put("v", 20161001);
        params.put("client_id", "K5BE5J2BYC5I00EK4Y3ITM4E33JHKI5YAWJFSUTV3SAMDHCS");
        params.put("client_secret", "RMV4QC42VKAWVGCXA0D54ITCPQJR42LK51XBHJCNDOJGPRNC");
        System.out.println(params);
        client.get(BASE_URL, params, responseHandler);
    }

    public static void findDetails(String id, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("v", 20161201);
        params.put("client_id", "K5BE5J2BYC5I00EK4Y3ITM4E33JHKI5YAWJFSUTV3SAMDHCS");
        params.put("client_secret", "RMV4QC42VKAWVGCXA0D54ITCPQJR42LK51XBHJCNDOJGPRNC");
        System.out.println(params);
        client.get(DETAILS_URL + id, params, jsonHttpResponseHandler);

    }

    public static void findTips(String id, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("v", 20161201);
        params.put("client_id", "K5BE5J2BYC5I00EK4Y3ITM4E33JHKI5YAWJFSUTV3SAMDHCS");
        params.put("client_secret", "RMV4QC42VKAWVGCXA0D54ITCPQJR42LK51XBHJCNDOJGPRNC");
        System.out.println(params);
        client.get(DETAILS_URL + id+"/tips", params, jsonHttpResponseHandler);
    }
}