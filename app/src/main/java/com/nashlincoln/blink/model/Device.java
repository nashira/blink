package com.nashlincoln.blink.model;

import java.util.List;

/**
 * Created by nash on 10/5/14.
 */
public class Device extends Model {
    public long id;
    public String name;
    public String interconnect;
    public int deviceTypeId;
    public List<Attribute> attributes;

    public transient State state = State.Nominal;
//    private transient DeviceType deviceType;

    public enum State {
        Added,
        Removed,
        Updated,
        Nominal
    }

//    public DeviceType getDeviceType() {
//        if (deviceType == null) {
//            for (DeviceType dt : Database.getInstance().mDeviceTypes) {
//                if (dt.id == deviceTypeId) {
//                    deviceType = dt;
//                    break;
//                }
//            }
//        }
//        return deviceType;
//    }

    public void setNominal() {
        state = State.Nominal;
        for (Attribute attribute : attributes) {
            attribute.reset();
        }

    }

    public void setLevel(int level) {
        Attribute attribute = attributes.get(1);
        if (attribute.getInt() != level) {
            attribute.setValue(level);
            state = State.Updated;
        }
    }

    public void setOn(boolean on) {
        Attribute attribute = attributes.get(0);
        if (on != attribute.getBool()) {
            attribute.setValue(on);
            state = State.Updated;
        }
    }

    public boolean isOn() {
        return attributes.get(0).getBool();
    }

    public int getLevel() {
        return attributes.get(1).getInt();
    }
}
