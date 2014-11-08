package com.nashlincoln.blink.network;

import com.nashlincoln.blink.content.Command;
import com.nashlincoln.blink.model.AttributeType;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceType;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by nash on 10/5/14.
 */
public interface BlinkApiInterface {
    @GET("/api/devices/")
    public void getDevices(Callback<List<Device>> callback);

    @GET("/api/device_types/")
    public void getDeviceTypes(Callback<List<DeviceType>> callback);

    @GET("/api/attribute_types/")
    public void getAttributeTypes(Callback<List<AttributeType>> callback);

    @POST("/api/commands/")
    void sendCommands(@Body List<Command> commands, Callback<Response> callback);
}
