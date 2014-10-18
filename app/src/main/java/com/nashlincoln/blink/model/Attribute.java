package com.nashlincoln.blink.model;

/**
* Created by nash on 10/5/14.
*/
public class Attribute extends Model {
    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_UINT8 = "UINT8";
    public static final String TRUE = "ON";
    public static final String FALSE = "OFF";


    public long id;
    public int attributeTypeId;
    public String value;

//    public String valueLocal;
//    public String dataType;

//    public String mInProgressValue;
//    public String mWaitingValue;

    public boolean getBool() {
        return value.equals(TRUE);
    }

    public int getInt() {
        return Integer.parseInt(value);
    }
}
