package com.nashlincoln.blink.model;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.network.BlinkApi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nash on 10/18/14.
 */
public class Database {
    private static Database sInstance;
    private final DaoSession mDaoSession;

    public static Database getInstance() {
        if (sInstance == null) {
            sInstance = new Database();
        }
        return sInstance;
    }

    public Database() {
        mDaoSession = BlinkApp.getDaoSession();
    }

    public void syncGroups() {
        final List<Command> commands = new ArrayList<>();
        for (Group group : mDaoSession.getGroupDao().loadAll()) {
            if (group.getState() == null) {
                group.setState(Device.STATE_NOMINAL);
            }
            switch (group.getState()) {

                case Device.STATE_UPDATED:
                    for (Device device : group.getDevices()) {
                        commands.add(Command.update(group, device));
                    }

                    break;
            }
        }

        if (commands.size() > 0) {
            BlinkApi.getClient().sendCommands(commands, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    for (Command command : commands) {
                        command.device.setNominal();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public void syncDevices() {
        final List<Command> commands = new ArrayList<>();
        for (Device device : mDaoSession.getDeviceDao().loadAll()) {
            Command command = null;
            if (device.getState() == null) {
                device.setState(Device.STATE_NOMINAL);
            }
            switch (device.getState()) {
                case Device.STATE_ADDED:
                    command = Command.add(device);
                    break;

                case Device.STATE_REMOVED:
                    command = Command.remove(device);
                    break;

                case Device.STATE_UPDATED:
                    command = Command.update(device);

                    break;
                case Device.STATE_NOMINAL:
                    break;
            }
            if (command != null) {
                commands.add(command);
            }
        }

        if (commands.size() > 0) {
            BlinkApi.getClient().sendCommands(commands, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    for (Command command : commands) {
                        command.device.setNominal();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public void fetchDeviceTypes() {
        BlinkApi.getClient().getDeviceTypes(new Callback<List<DeviceType>>() {
            @Override
            public void success(List<DeviceType> deviceTypes, Response response) {
                DeviceTypeDao deviceTypeDao = mDaoSession.getDeviceTypeDao();
                deviceTypeDao.insertOrReplaceInTx(deviceTypes);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void fetchAttributeTypes() {
        BlinkApi.getClient().getAttributeTypes(new Callback<List<AttributeType>>() {
            @Override
            public void success(List<AttributeType> attributeTypes, Response response) {
                AttributeTypeDao attributeTypeDao = mDaoSession.getAttributeTypeDao();
                attributeTypeDao.insertOrReplaceInTx(attributeTypes);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public void fetchDevices() {
        BlinkApi.getClient().getDevices(new Callback<List<Device>>() {
            @Override
            public void success(final List<Device> devices, Response response) {
                final DeviceDao deviceDao = mDaoSession.getDeviceDao();
                mDaoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        for (Device device : devices) {
                            device.setAttributableType(Device.ATTRIBUTABLE_TYPE);
                            device.flushAttributes();
                            device.resetAttributes();
                        }
                    }
                });
                deviceDao.insertInTx(devices);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
