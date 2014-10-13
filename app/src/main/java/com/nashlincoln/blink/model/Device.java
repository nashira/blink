package com.nashlincoln.blink.model;

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
public class Device extends Model {
    public int deviceId;
    public String userName;
    List<Attr> attrs;

    public boolean isOn() {
        return attrs.get(0).value_get.equals("ON");
    }

    public int getValue() {
        return Integer.parseInt(attrs.get(1).value_get);
    }

    public void setOn(boolean on) {
        if (on == isOn()) {
            return;
        }
        final String value = on ? "ON" : "OFF";

        final Attr attr = attrs.get(0);

        if (attr.mInProgressValue != null) {
            attr.mWaitingValue = value;
            return;
        }
        attr.mInProgressValue = value;

        DeviceList.get().event(new Event(Type.SetOn, Status.InProgress));
        BlinkApi.getClient().setValue(deviceId, 1, value, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                attr.value_get = attr.mInProgressValue;
                attr.mInProgressValue = null;
                DeviceList.get().event(new Event(Type.SetOn, Status.Success));

                if (attr.mWaitingValue != null) {
                    String val = attr.mWaitingValue;
                    attr.mWaitingValue = null;
                    setOn(val.equals("ON"));
                }
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
        final Attr attr = attrs.get(1);

        if (attr.mInProgressValue != null) {
            attr.mWaitingValue = stringValue;
            return;
        }
        attr.mInProgressValue = stringValue;

        DeviceList.get().event(new Event(Type.SetValue, Status.InProgress));
        BlinkApi.getClient().setValue(deviceId, 2, value, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                attr.value_get = attr.mInProgressValue;
                attr.mInProgressValue = null;
                DeviceList.get().event(new Event(Type.SetOn, Status.Success));

                if (attr.mWaitingValue != null) {
                    String val = attr.mWaitingValue;
                    attr.mWaitingValue = null;
                    setValue(Integer.parseInt(val));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DeviceList.get().event(new Event(Type.SetValue, Status.Failure));
            }
        });
    }
}
