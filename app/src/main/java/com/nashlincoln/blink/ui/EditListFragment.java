package com.nashlincoln.blink.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.DeviceLoader;
import com.nashlincoln.blink.model.Device;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* Created by nash on 11/10/14.
*/
public class EditListFragment extends BlinkListFragment implements LoaderManager.LoaderCallbacks<List<Device>> {

    private DeviceAdapter mAdapter;
    private EditText mNameView;
    private String mName;
    private Map<Long, Device> mSelectedDevices;
    private Set<Long> mChecked;
    private long mId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedDevices = new HashMap<>();
        mChecked = new HashSet<>();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            if (getArguments().containsKey(BlinkApp.EXTRA_NAME)) {
                mName = getArguments().getString(BlinkApp.EXTRA_NAME);
            }

            if (getArguments().containsKey(BlinkApp.EXTRA_ID)) {
                mId = getArguments().getLong(BlinkApp.EXTRA_ID);
            }

            if (getArguments().containsKey(BlinkApp.EXTRA_DEVICE_IDS)) {
                long[] longArray = getArguments().getLongArray(BlinkApp.EXTRA_DEVICE_IDS);
                for (long id : longArray) {
                    mChecked.add(id);
                }
            }
        }
    }

    @Override
    protected void onFabClick(View view) {
        Intent intent = new Intent();

        long[] deviceIds = new long[mChecked.size()];
        int index = 0;
        for (Long id : mChecked) {
            deviceIds[index++] = id;
        }

        if (mId != -1) {
            intent.putExtra(BlinkApp.EXTRA_ID, mId);
        }
        intent.putExtra(BlinkApp.EXTRA_NAME, mNameView.getText().toString().trim());
        intent.putExtra(BlinkApp.EXTRA_DEVICE_IDS, deviceIds);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mId != -1) {
            inflater.inflate(R.menu.activity_edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View header = inflater.inflate(R.layout.edit_text_dialog, null);
        mNameView = (EditText) header.findViewById(R.id.edit_text);
        mListView.addHeaderView(header, null, false);
        mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white_24dp));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mName != null) {
            mNameView.setText(mName);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new DeviceAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Device>> onCreateLoader(int id, Bundle args) {
        return new DeviceLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Device>> loader, List<Device> data) {
        for (Device device : data) {
            if (mChecked.contains(device.getId())) {
                mSelectedDevices.put(device.getId(), device);
            }
        }
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Device>> loader) {

    }

    private void updateName() {
        StringBuilder sb = new StringBuilder();

        for (Device device : mSelectedDevices.values()) {
            sb.append(device.getName()).append(", ");
        }

        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }

        mNameView.setText(sb.toString());
    }

    class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.device_select_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(holder, position);

            return convertView;
        }

        private void bindView(Holder holder, int position) {
            Device device = getItem(position);
            holder.position = position;
            holder.checkBox.setChecked(mChecked.contains(device.getId()));
            holder.textView.setText(device.getName());
        }

        class Holder implements View.OnClickListener {
            int position;
            CheckBox checkBox;
            TextView textView;

            public Holder(View view) {
                view.setTag(this);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                textView = (TextView) view.findViewById(R.id.text);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                checkBox.toggle();

                Device device = getItem(position);
                if (checkBox.isChecked()) {
                    mSelectedDevices.put(device.getId(), device);
                    mChecked.add(device.getId());
                } else {
                    mSelectedDevices.remove(device.getId());
                    mChecked.remove(device.getId());
                }
                updateName();
            }
        }
    }
}
