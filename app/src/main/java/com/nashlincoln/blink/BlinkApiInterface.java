package com.nashlincoln.blink;

import com.nashlincoln.blink.model.Device;

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
            @Field("dev") int deviceId,
            @Field("attr") int attributeId,
            @Field("val") int value,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/api/api.php")
    public void setValue(
            @Field("dev") int deviceId,
            @Field("attr") int attributeId,
            @Field("val") String value,
            Callback<Response> callback);

    @GET("/api/api_devices.php")
    public void getDevices(Callback<List<Device>> callback);
}
