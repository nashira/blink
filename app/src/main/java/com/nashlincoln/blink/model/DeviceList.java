package com.nashlincoln.blink.model;

import android.util.Log;

import com.nashlincoln.blink.BlinkApi;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.event.Status;
import com.nashlincoln.blink.event.Type;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nash on 10/5/14.
 */
public class DeviceList extends Model {
    private static final String TAG = "DeviceList";
    private static DeviceList sInstance;
    private List<Device> mDevices;

    public static DeviceList get() {
        if (sInstance == null) {
            sInstance = new DeviceList();
        }

        return sInstance;
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    public void fetch() {
        event(new Event(Type.Fetch, Status.InProgress));
        BlinkApi.getClient().getDevices(new Callback<List<Device>>() {
            @Override
            public void success(List<Device> devices, Response response) {
                Log.d(TAG, "devices: " + devices.size());
                mDevices = devices;
                event(new Event(Type.Fetch, Status.Success));
            }

            @Override
            public void failure(RetrofitError error) {
                event(new Event(Type.Fetch, Status.Failure));
            }
        });
    }
}
