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
import com.nashlincoln.blink.model.Scene;
import com.nashlincoln.blink.model.SceneDevice;
import com.nashlincoln.blink.model.SceneDeviceDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nash on 10/24/14.
 */
public class AddSceneDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Device> devices = BlinkApp.getDaoSession().getDeviceDao().loadAll();
        String[] names = new String[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            names[i] = devices.get(i).getName();
        }
        final boolean[] checked = new boolean[devices.size()];

        final List<String> joinedName = new ArrayList<String>();

        final Scene scene = new Scene();
        final Map<Integer, SceneDevice> sceneDevices = new HashMap<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.device_selector_title);
        builder.setMultiChoiceItems(names, checked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        SceneDevice sceneDevice = SceneDevice.newInstance();
                        sceneDevice.setDeviceId(devices.get(which).getId());
                        sceneDevices.put(which, sceneDevice);
                        joinedName.add(devices.get(which).getName());
                    } else {
                        sceneDevices.remove(which);
                        joinedName.remove(devices.get(which).getName());
                    }
                }})
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (sceneDevices.isEmpty()) {
                            return;
                        }

                        scene.setName(TextUtils.join(", ", joinedName));
                        BlinkApp.getDaoSession().getSceneDao().insert(scene);
                        SceneDeviceDao sceneDeviceDao = BlinkApp.getDaoSession().getSceneDeviceDao();
                        for (SceneDevice sceneDevice : sceneDevices.values()) {
                            sceneDevice.setSceneId(scene.getId());
                            sceneDeviceDao.insert(sceneDevice);
                            sceneDevice.copyAttributes(devices.get(0).getAttributes());
                        }
                        scene.resetSceneDeviceList();
                        Event.broadcast(Scene.KEY);
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
