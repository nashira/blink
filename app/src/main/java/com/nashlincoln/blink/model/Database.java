package com.nashlincoln.blink.model;

import com.nashlincoln.blink.network.BlinkApi;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nash on 10/18/14.
 */
public class Database {
    private static Database sInstance;
    public List<DeviceType> mDeviceTypes;
    public List<AttributeType> mAttributeTypes;
    public List<Device> mDevices;

    public static Database getInstance() {
        if (sInstance == null) {
            sInstance = new Database();
        }
        return sInstance;
    }

    public void fetchDeviceTypes() {
        BlinkApi.getClient().getDeviceTypes(new Callback<List<DeviceType>>() {
            @Override
            public void success(List<DeviceType> deviceTypes, Response response) {
                mDeviceTypes = deviceTypes;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void fetchAttributeTypes() {
        BlinkApi.getClient().getAttributeTypes(new Callback<List<AttributeType>>() {
            @Override
            public void success(List<AttributeType> attributeTypes, Response response) {
                mAttributeTypes = attributeTypes;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public void fetchDevices() {
        BlinkApi.getClient().getDevices(new Callback<List<Device>>() {
            @Override
            public void success(List<Device> devices, Response response) {
                mDevices = devices;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
