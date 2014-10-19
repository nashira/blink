package com.nashlincoln.blink.model1;

import com.nashlincoln.blink.network.BlinkApi;

import java.util.ArrayList;
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

    public void syncLocalToServer() {
        final List<Command> commands = new ArrayList<>();
        for (Device device : mDevices) {
            Command command = null;
            if (device.getState() == null) {
                device.setState(Device.STATE_NOMINAL);
            }
            switch (device.getState()) {
                case Device.STATE_ADDED:
                    command = Command.add(device);
                    break;

                case Device.STATE_REMOVED:
                    command = Command.remove(device);
                    break;

                case Device.STATE_UPDATED:
                    command = Command.update(device);

                    break;
                case Device.STATE_NOMINAL:
                    break;
            }
            if (command != null) {
                commands.add(command);
            }
        }

        if (commands.size() > 0) {
            BlinkApi.getClient().sendCommands(commands, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    for (Command command : commands) {
                        command.device.setNominal();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
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
