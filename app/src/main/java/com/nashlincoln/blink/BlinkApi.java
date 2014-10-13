package com.nashlincoln.blink;

import retrofit.RestAdapter;

/**
 * Created by nash on 10/5/14.
 */
public class BlinkApi {
    private static BlinkApiInterface sService;
    private static String sApiEndpoint = "http://10.0.0.149";

    private BlinkApi() {}

    public static BlinkApiInterface getClient() {

        if (sService == null) {

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(sApiEndpoint)
//                    .setLogLevel(RestAdapter.LogLevel.BASIC)
//                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            sService = restAdapter.create(BlinkApiInterface.class);
        }

        return sService;
    }
}
