package com.nashlincoln.blink.content;

import android.util.Log;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.model.Attribute;
import com.nashlincoln.blink.model.AttributeType;
import com.nashlincoln.blink.model.AttributeTypeDao;
import com.nashlincoln.blink.model.DaoSession;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceDao;
import com.nashlincoln.blink.model.DeviceType;
import com.nashlincoln.blink.model.DeviceTypeDao;
import com.nashlincoln.blink.model.Group;
import com.nashlincoln.blink.model.GroupDao;
import com.nashlincoln.blink.model.GroupDevice;
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.nfc.NfcCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nash on 10/18/14.
 */
public class Syncro {
    private static final String TAG = "Syncro";
    public static final String CONNECTION = "Connection";
    private static Syncro sInstance;
    private final DaoSession mDaoSession;
    private boolean mIsConnected = false;

    public static Syncro getInstance() {
        if (sInstance == null) {
            sInstance = new Syncro();
        }
        return sInstance;
    }

    private Syncro() {
        mDaoSession = BlinkApp.getDaoSession();
    }

    public void syncDevices() {
        if (!mIsConnected) {
            return;
        }

        final List<Command> commands = new ArrayList<>();

        addDeviceCommands(commands);
        addGroupCommands(commands);
        addGroupDeviceCommands(commands);

        if (commands.size() > 0) {
            BlinkApi.getClient().sendCommands(commands, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    final boolean[] needsRefresh = {false};
                    BlinkApp.getDaoSession().runInTx(new Runnable() {
                        @Override
                        public void run() {
                            for (Command command : commands) {
                                needsRefresh[0] |= command.action.equals(Command.ADD);
                                needsRefresh[0] |= command.action.equals(Command.UPDATE_GROUP);

                                if (command.device != null) {
                                    command.device.setNominal();
                                }

                                if (command.group != null) {
                                    command.group.setNominal();
                                }

                                if (command.groupDevice != null) {
                                    command.groupDevice.setNominal();
                                }
                            }
                        }
                    });

                    Event.broadcast(Device.KEY);
                    Event.broadcast(Group.KEY);

                    if (needsRefresh[0]) {
                        fetchDevices();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void addGroupDeviceCommands(List<Command> commands) {
        for (GroupDevice groupDevice : mDaoSession.getGroupDeviceDao().loadAll()) {
            Command command = null;
            if (groupDevice.getState() == null) {
                groupDevice.setState(BlinkApp.STATE_NOMINAL);
            }
            switch (groupDevice.getState()) {
                case BlinkApp.STATE_ADDED:
                    command = Command.add(groupDevice);
                    break;

                case BlinkApp.STATE_REMOVED:
                    command = Command.remove(groupDevice);
                    break;
            }
            if (command != null) {
                commands.add(command);
            }
        }
    }

    private void addGroupCommands(List<Command> commands) {
        for (Group group : mDaoSession.getGroupDao().loadAll()) {
            Command command = null;
            if (group.getState() == null) {
                group.setState(BlinkApp.STATE_NOMINAL);
            }
            switch (group.getState()) {
                case BlinkApp.STATE_ADDED:
                    command = Command.add(group);
                    break;

                case BlinkApp.STATE_REMOVED:
                    command = Command.remove(group);
                    break;

                case BlinkApp.STATE_NAME_SET:
                    command = Command.setName(group);
                    break;

                case BlinkApp.STATE_UPDATED:
                    command = Command.update(group);
                    break;

                case BlinkApp.STATE_NOMINAL:
                    break;
            }
            if (command != null) {
                commands.add(command);
            }
        }
    }

    private void addDeviceCommands(List<Command> commands) {
        for (Device device : mDaoSession.getDeviceDao().loadAll()) {
            Command command = null;
            if (device.getState() == null) {
                device.setState(BlinkApp.STATE_NOMINAL);
            }
            switch (device.getState()) {
                case BlinkApp.STATE_ADDED:
                    command = Command.add(device);
                    break;

                case BlinkApp.STATE_REMOVED:
                    command = Command.remove(device);
                    break;

                case BlinkApp.STATE_UPDATED:
                    command = Command.update(device);
                    break;

                case BlinkApp.STATE_NAME_SET:
                    command = Command.setName(device);
                    break;

                case BlinkApp.STATE_NOMINAL:
                    break;
            }
            if (command != null) {
                commands.add(command);
            }
        }
    }

    public void fetchDeviceTypes() {
        if (!mIsConnected) {
            return;
        }
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
        if (!mIsConnected) {
            return;
        }
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

    public void fetchDevicesAndGroups() {
        fetchDevices();
        final String key = Device.KEY + "/fetch";
        Event.observe(key, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Event.ignore(key, this);
                fetchGroups();
            }
        });
    }


    public void fetchDevices() {
        if (!mIsConnected) {
            return;
        }
        BlinkApi.getClient().getDevices(new Callback<List<Device>>() {
            @Override
            public void success(final List<Device> devices, Response response) {
                final DeviceDao deviceDao = mDaoSession.getDeviceDao();
                mDaoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        List<Device> currentDevices = deviceDao.loadAll();
                        Set<Long> newIds = new HashSet<>();
                        for (Device device : devices) {
                            newIds.add(device.getId());
                            device.setAttributableType(Device.ATTRIBUTABLE_TYPE);
                            device.flushAttributes();
                            device.resetAttributes();
                            deviceDao.insertOrReplace(device);
                        }

                        for (Device device : currentDevices) {
                            if (!newIds.contains(device.getId())) {
                                device.deleteWithReferences();
                            }
                        }

                    }
                });
                mDaoSession.clear();
                Event.broadcast(Device.KEY);
                Event.broadcast(Device.KEY + "/fetch");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void fetchGroups() {
        if (!mIsConnected) {
            return;
        }
        BlinkApi.getClient().getGroups(new Callback<List<Group>>() {
            @Override
            public void success(final List<Group> groups, Response response) {
                final GroupDao groupDao = mDaoSession.getGroupDao();
                mDaoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        List<Group> currentGroups = groupDao.loadAll();
                        Set<Long> newIds = new HashSet<>();
                        for (Group group : groups) {
                            newIds.add(group.getId());
                            group.setAttributableType(Group.ATTRIBUTABLE_TYPE);
                            group.flushAttributes();
                            group.resetAttributes();
                            group.flushGroupDevices();
                            group.resetGroupDeviceList();
                            groupDao.insertOrReplace(group);
                        }

                        for (Group group : currentGroups) {
                            if (!newIds.contains(group.getId())) {
                                group.deleteWithReferences();
                            }
                        }

                    }
                });
                mDaoSession.clear();
                Event.broadcast(Group.KEY);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public synchronized void onConnected(boolean isConnected) {
        Log.d(TAG, "onConnected: " + isConnected);
        boolean previous = mIsConnected;
        mIsConnected = isConnected;

        if (mIsConnected && !previous) {
            Event.observe(Device.KEY, new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    Event.ignore(Device.KEY, this);
                    syncDevices();
                }
            });
            fetchDevicesAndGroups();
        }

        Event.broadcast(CONNECTION, mIsConnected);
    }

    public void applyNfcCommands(final List<NfcCommand> commands) {
        final DeviceDao deviceDao = mDaoSession.getDeviceDao();
        mDaoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                for (NfcCommand command : commands) {
                    Device device = deviceDao.load(command.i);
                    if (device != null) {
                        for (NfcCommand.Update update : command.u) {
                            if (update.i == 1) {
                                device.setOn(update.v.equals(Attribute.ON));
                            } else if (update.i == 2) {
                                device.setLevel(Integer.parseInt(update.v));
                            }
                        }
                    }
                }
            }
        });
        syncDevices();
    }
}
