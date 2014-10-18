package com.nashlincoln.blink.network;

import com.nashlincoln.blink.model.AttributeType;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceType;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by nash on 10/5/14.
 */
public interface BlinkApiInterface {
    @FormUrlEncoded
    @POST("/api/api.php")
    public void setValue(
            @Field("dev") long deviceId,
            @Field("attr") int attributeId,
            @Field("val") int value,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/api/api.php")
    public void setValue(
            @Field("dev") long deviceId,
            @Field("attr") int attributeId,
            @Field("val") String value,
            Callback<Response> callback);

    @GET("/api/devices/")
    public void getDevices(Callback<List<Device>> callback);

    @GET("/api/device_types/")
    public void getDeviceTypes(Callback<List<DeviceType>> callback);

    @GET("/api/attribute_types/")
    public void getAttributeTypes(Callback<List<AttributeType>> callback);
}
