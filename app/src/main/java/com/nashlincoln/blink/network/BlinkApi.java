package com.nashlincoln.blink.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nashlincoln.blink.app.BlinkApp;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by nash on 10/5/14.
 */
public class BlinkApi {
    private static BlinkApiInterface sService;
    private static Gson sGson;

    private BlinkApi() {
    }

    public static BlinkApiInterface getClient() {

        if (sService == null) {
            createService(BlinkApp.getApp().getHost());
        }

        return sService;
    }

    public static Gson getGson() {

        if (sService == null) {
            createService(BlinkApp.getApp().getHost());
        }

        return sGson;
    }

    public static void createService(String host) {
        sGson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        if (host == null || host.equals("")) {
            sService = null;
            return;
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(sGson))
                .setEndpoint(BlinkApp.getApp().getHost())
                .setLogLevel(RestAdapter.LogLevel.BASIC)
//                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        sService = restAdapter.create(BlinkApiInterface.class);
    }
}
