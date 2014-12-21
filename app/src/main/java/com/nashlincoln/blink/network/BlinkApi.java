package com.nashlincoln.blink.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.event.Event;

import java.io.IOException;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by nash on 10/5/14.
 */
public class BlinkApi {
    private static BlinkApiInterface sService;
    private static Gson sGson;
    private static int sRequestCount = 0;

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
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        sRequestCount++;
                        Event.broadcast("network");
                    }
                })
                .setClient(new OkClient(){
                    @Override
                    public Response execute(Request request) throws IOException {
                        Response response = super.execute(request);
                        sRequestCount--;
                        Event.broadcast("network");
                        return response;
                    }
                })
                .setEndpoint(BlinkApp.getApp().getHost())
                .setLogLevel(RestAdapter.LogLevel.BASIC)
//                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
//                    .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        sService = restAdapter.create(BlinkApiInterface.class);
    }

    public static int getRequestCount() {
        return sRequestCount;
    }
}
