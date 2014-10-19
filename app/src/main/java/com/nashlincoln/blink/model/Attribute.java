package com.nashlincoln.blink.model;

/**
* Created by nash on 10/5/14.
*/
public class Attribute extends Model {
    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_UINT8 = "UINT8";
    public static final String ON = "ON";
    public static final String OFF = "OFF";


    public long id;
    public int attributeTypeId;
    public String value;
    public String valueLocal;

//    public String dataType;

//    public String mInProgressValue;
//    public String mWaitingValue;

    public boolean getBool() {
        return value.equals(ON);
    }

    public int getInt() {
        return Integer.parseInt(value);
    }

    public boolean isChanged() {
        return !(valueLocal == null || value.equals(valueLocal));
    }

    public void reset() {
        if (isChanged()) {
            value = valueLocal;
            valueLocal = null;
        }
    }

    public void setValue(String value) {
        valueLocal = value;
    }

    public void setValue(boolean on) {
        valueLocal = on ? ON : OFF;
    }

    public void setValue(int value) {
        valueLocal = String.valueOf(value);
    }
}
