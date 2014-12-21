package com.nashlincoln.blink.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.DeviceTypeLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.DeviceType;
import com.nashlincoln.blink.network.BlinkApi;

import java.util.List;

/**
 * Created by nash on 12/6/14.
 */
public class AddDeviceFragment extends BlinkListFragment
        implements AdapterView.OnItemClickListener {
    private static final String TAG = "AddDeviceFragment";
//    private DeviceType mDeviceType;
    private String mInterconnect;
    private DeviceTypeAdapter mAdapter;

    @Override
    protected void onFabClick(View view) {
        if (mInterconnect != null) {
//            Intent intent = new Intent();
//            intent.putExtra(BlinkApp.EXTRA_ID, mDeviceType.getId());
//            getActivity().setResult(Activity.RESULT_OK, intent);
            Device device = new Device();
//            device.setDeviceType(mDeviceType);
            device.setInterconnect(mInterconnect);
            device.setAttributableType(Device.ATTRIBUTABLE_TYPE);
            device.setState(BlinkApp.STATE_ADDED);
            BlinkApp.getDaoSession().getDeviceDao().insert(device);
            Syncro.getInstance().syncDevices();
            Log.d(TAG, "add: " + device.getId());
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mFabButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white_24dp));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new DeviceTypeAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mAdapter.add("ZIGBEE");
//        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeviceTypeAdapter.Holder holder = (DeviceTypeAdapter.Holder) view.getTag();
        mInterconnect = mAdapter.getItem(holder.position);
        mAdapter.notifyDataSetChanged();
//        DeviceType deviceType = mAdapter.getItem(holder.position);
//        if (deviceType.isEnabled()) {
//            mDeviceType = deviceType;
//            mAdapter.notifyDataSetChanged();
//        }
    }

//    @Override
//    public Loader<List<DeviceType>> onCreateLoader(int id, Bundle args) {
//        return new DeviceTypeLoader(getActivity());
//    }
//
//    @Override
//    public void onLoadFinished(Loader<List<DeviceType>> loader, List<DeviceType> data) {
//        mAdapter.clear();
//        mAdapter.addAll(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<List<DeviceType>> loader) {
//
//    }

    class DeviceTypeAdapter extends ArrayAdapter<String> {

        public DeviceTypeAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.device_type_select_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(holder, position);

            return convertView;
        }

        private void bindView(Holder holder, int position) {
            String interconnect = getItem(position);
            holder.position = position;
            holder.radioButton.setChecked(interconnect.equals(mInterconnect));
            holder.textView.setText(interconnect);
//            holder.radioButton.setEnabled(deviceType.isEnabled());
        }

        class Holder {
            int position;
            RadioButton radioButton;
            TextView textView;

            public Holder(View view) {
                view.setTag(this);
                radioButton = (RadioButton) view.findViewById(R.id.radio);
                textView = (TextView) view.findViewById(R.id.text);
            }
        }
    }
}
