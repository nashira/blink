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
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.nfc.NfcCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
    private boolean mIsConnected = true;

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

                case Device.STATE_NAME_SET:
                    command = Command.setName(device);
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
                    BlinkApp.getDaoSession().runInTx(new Runnable() {
                        @Override
                        public void run() {
                            for (Command command : commands) {
                                command.device.setNominal();
                            }
                        }
                    });
                    Event.broadcast(Device.KEY);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
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
                        for (Device device : devices) {
                            device.setAttributableType(Device.ATTRIBUTABLE_TYPE);
                            device.flushAttributes();
                            device.resetAttributes();
                            deviceDao.insertOrReplace(device);
                        }
                    }
                });
                mDaoSession.clear();
                Event.broadcast(Device.KEY);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void refreshDevices() {
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
                        for (Device device : devices) {
                            Device current = deviceDao.load(device.getId());
                            if (current == null) {
                                device.setAttributableType(Device.ATTRIBUTABLE_TYPE);
                                device.flushAttributes();
                                device.resetAttributes();
                                deviceDao.insertOrReplace(device);
                            } else {
                                current.updateFrom(device);
                            }
                        }
                    }
                });

                mDaoSession.clear();
                Event.broadcast(Device.KEY);
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
            refreshDevices();
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
