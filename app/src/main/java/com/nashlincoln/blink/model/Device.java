package com.nashlincoln.blink.model;

import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.event.Status;
import com.nashlincoln.blink.event.Type;
import com.nashlincoln.blink.network.BlinkApi;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nash on 10/5/14.
 */
public class Device extends Model {
    public long id;
    public String name;
    public String interconnect;
    public int deviceTypeId;
    public List<Attribute> attributes;

    private transient DeviceType deviceType;

    public DeviceType getDeviceType() {
        if (deviceType == null) {
            for (DeviceType dt : Database.getInstance().mDeviceTypes) {
                if (dt.id == deviceTypeId) {
                    deviceType = dt;
                    break;
                }
            }
        }
        return deviceType;
    }

    public boolean isOn() {
        return attributes.get(0).value.equals("ON");
    }

    public int getValue() {
        return Integer.parseInt(attributes.get(1).value);
    }

    public void setOn(boolean on) {
        if (on == isOn()) {
            return;
        }
        final String value = on ? "ON" : "OFF";

        final Attribute attr = attributes.get(0);

        DeviceList.get().event(new Event(Type.SetOn, Status.InProgress));
        BlinkApi.getClient().setValue(id, 1, value, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                DeviceList.get().event(new Event(Type.SetOn, Status.Success));
            }

            @Override
            public void failure(RetrofitError error) {
                DeviceList.get().event(new Event(Type.SetOn, Status.Failure));
            }
        });
    }

    public void setValue(final int value) {
        if (value == getValue()) {
            return;
        }
        String stringValue = String.valueOf(value);
        final Attribute attr = attributes.get(1);


        DeviceList.get().event(new Event(Type.SetValue, Status.InProgress));
        BlinkApi.getClient().setValue(id, 2, value, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                DeviceList.get().event(new Event(Type.SetValue, Status.Success));
            }

            @Override
            public void failure(RetrofitError error) {
                DeviceList.get().event(new Event(Type.SetValue, Status.Failure));
            }
        });
    }
}
