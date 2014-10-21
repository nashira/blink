package com.nashlincoln.blink.content;

import android.content.Context;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.model.Device;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class DeviceLoader extends ModelLoader<List<Device>> {

    public DeviceLoader(Context context) {
        super(context);
    }

    @Override
    public List<Device> fetch() {
        return BlinkApp.getDaoSession().getDeviceDao().loadAll();
    }

    @Override
    public String getKey() {
        return Device.KEY;
    }
}
