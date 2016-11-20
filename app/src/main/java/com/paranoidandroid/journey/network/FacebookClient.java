package com.paranoidandroid.journey.network;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

/**
 * Get user data from Facebook Graph API.
 */
public class FacebookClient {

    /**
     * Remove the read permissions that we acquired when the user signed up.
     */
    public static void revokeAppPermissions() {
        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me/permissions/", null, HttpMethod.DELETE);
        request.executeAsync();
    }
}
