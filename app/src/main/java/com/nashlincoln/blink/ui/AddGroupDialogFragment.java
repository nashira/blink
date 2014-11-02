package com.nashlincoln.blink.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.Group;
import com.nashlincoln.blink.model.GroupDevice;
import com.nashlincoln.blink.model.GroupDeviceDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nash on 10/24/14.
 */
public class AddGroupDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Device> devices = BlinkApp.getDaoSession().getDeviceDao().loadAll();
        String[] names = new String[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            names[i] = devices.get(i).getName();
        }
        final boolean[] checked = new boolean[devices.size()];

        final List<String> joinedName = new ArrayList<String>();

        final Group group = Group.newInstance();
        final Map<Integer, GroupDevice> groupDevices = new HashMap<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.device_selector_title);
        builder.setMultiChoiceItems(names, checked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        GroupDevice groupDevice = new GroupDevice();
                        groupDevice.setDeviceId(devices.get(which).getId());
                        groupDevices.put(which, groupDevice);
                        joinedName.add(devices.get(which).getName());
                    } else {
                        groupDevices.remove(which);
                        joinedName.remove(devices.get(which).getName());
                    }
                }})
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (groupDevices.isEmpty()) {
                            return;
                        }

                        group.setName(TextUtils.join(", ", joinedName));
                        BlinkApp.getDaoSession().getGroupDao().insert(group);
                        GroupDeviceDao groupDeviceDao = BlinkApp.getDaoSession().getGroupDeviceDao();
                        for (GroupDevice groupDevice : groupDevices.values()) {
                            groupDevice.setGroupId(group.getId());
                            groupDeviceDao.insert(groupDevice);
                        }
                        group.copyAttributes(devices.get(0).getAttributes());
                        group.resetGroupDeviceList();
                        Event.broadcast(Group.KEY);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User declined.
                    }
                });
        return builder.create();
    }
}
