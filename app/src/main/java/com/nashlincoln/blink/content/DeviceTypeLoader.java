package com.nashlincoln.blink.content;

import android.content.Context;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceType;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class DeviceTypeLoader extends ModelLoader<List<DeviceType>> {

    public DeviceTypeLoader(Context context) {
        super(context);
    }

    @Override
    public List<DeviceType> fetch() {
        return BlinkApp.getDaoSession().getDeviceTypeDao().loadAll();
    }

    @Override
    public String getKey() {
        return Device.KEY;
    }
}
