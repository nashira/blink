package com.nashlincoln.blink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.event.Type;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceList;
import com.nashlincoln.blink.model.Model;

/**
 * Created by nash on 10/5/14.
 */
public class BlinkActivity extends Activity implements Model.OnEvent {

    private static final String TAG = "BlinkActivity";
    private ListView mListView;
    private DeviceAdapter mAdapter;
    private boolean mToggleAfterFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new DeviceAdapter(this);
        mListView.setAdapter(mAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Log.d(TAG, "tag != null");
            Log.d(TAG, "devices: " + DeviceList.get().getDevices().size());

            mToggleAfterFetch = true;
//            if (DeviceList.get().getDevices().size() > 0) {
//                toggleLights();
//            } else {
//                mToggleAfterFetch = true;
//            }
        }
    }

    private void toggleLights() {
        boolean isOn = false;
        for (Device device : DeviceList.get().getDevices()) {
            if (device.isOn()) {
                isOn = true;
                device.setOn(false);
            }
        }
        if (!isOn) {
            for (Device device : DeviceList.get().getDevices()) {
                device.setOn(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceList.get().addOnEvent(this);
        DeviceList.get().fetch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DeviceList.get().removeOnEvent(this);
    }

    class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.device_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(position, holder);

            return convertView;
        }

        private void bindView(int position, Holder holder) {
            Device device = getItem(position);
            holder.position = position;
            holder.textView.setText(device.userName);
            holder.checkBox.setChecked(device.isOn());
            holder.seekBar.setProgress(device.getValue());
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
            int position;
            TextView textView;
            CheckBox checkBox;
            SeekBar seekBar;

            Holder(View view) {
                view.setTag(this);
                textView = (TextView) view.findViewById(R.id.text);
                checkBox = (CheckBox) view.findViewById(R.id.check_box);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                seekBar.setMax(255);
                checkBox.setOnCheckedChangeListener(this);
                seekBar.setOnSeekBarChangeListener(this);
            }



            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                Device device = getItem(position);
                device.setOn(b);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Device device = getItem(position);
                device.setValue(seekBar.getProgress());
            }
        }
    }

    @Override
    public void onEvent(Model model, Event event) {
        switch (event.status) {
            case InProgress:
                break;
            case Success:
                if (event.type == Type.Fetch) {
                    mAdapter.clear();
                    mAdapter.addAll(DeviceList.get().getDevices());
                    if (mToggleAfterFetch) {
                        mToggleAfterFetch = false;
                        toggleLights();
                    }
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case Failure:
                break;
        }
    }
}
