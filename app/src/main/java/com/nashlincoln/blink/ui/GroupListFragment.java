package com.nashlincoln.blink.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.GroupLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.model.DaoSession;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.Group;
import com.nashlincoln.blink.model.GroupDevice;
import com.nashlincoln.blink.model.Scene;
import com.nashlincoln.blink.model.SceneDevice;
import com.nashlincoln.blink.nfc.NfcUtils;
import com.nashlincoln.blink.widget.DeviceSummary;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class GroupListFragment extends BlinkListFragment {
    private static final String TAG = "GroupListFragment";
    private static final int REQUEST_EDIT = 123;
    private static final int REQUEST_ADD = 124;
    private GroupAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new GroupAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    protected void onFabClick(View view) {

        Intent intent = getAddEditIntent(null);
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "" + requestCode + " " + resultCode);
        final DaoSession daoSession = BlinkApp.getDaoSession();

        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            final String name = data.getStringExtra(BlinkApp.EXTRA_NAME);
            final long[] ids = data.getLongArrayExtra(BlinkApp.EXTRA_DEVICE_IDS);
            final long id = data.getLongExtra(BlinkApp.EXTRA_ID, -1);

            Group group = daoSession.getGroupDao().load(id);
            group.onEdit(name, ids);

        } else if (requestCode == REQUEST_ADD && resultCode == Activity.RESULT_OK && data != null) {
            final String name = data.getStringExtra(BlinkApp.EXTRA_NAME);
            final long[] ids = data.getLongArrayExtra(BlinkApp.EXTRA_DEVICE_IDS);

            Group.addNewGroup(name, ids);
        }
    }

    private Intent getAddEditIntent(Group group) {
        Intent intent = new Intent(getActivity(), EditListActivity.class);
        if (group != null) {
            intent.putExtra(BlinkApp.EXTRA_ID, group.getId());
            intent.putExtra(BlinkApp.EXTRA_NAME, group.getName());
            long[] deviceIds = new long[group.getGroupDeviceList().size()];
            int index = 0;
            for (GroupDevice groupDevice : group.getGroupDeviceList()) {
                deviceIds[index++] = groupDevice.getDeviceId();
            }

            intent.putExtra(BlinkApp.EXTRA_DEVICE_IDS, deviceIds);
        }
        return intent;
    }

    private LoaderManager.LoaderCallbacks<List<Group>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Group>>() {
        @Override
        public Loader<List<Group>> onCreateLoader(int i, Bundle bundle) {
            return new GroupLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> listLoader, List<Group> groups) {
            mAdapter.clear();
            if (groups != null) {
                mAdapter.addAll(groups);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Group>> listLoader) {

        }
    };

    class GroupAdapter extends ArrayAdapter<Group> {

        public GroupAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.group_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(position, holder);

            return convertView;
        }

        private void bindView(int position, Holder holder) {
            Group group = getItem(position);
            holder.position = position;
            holder.textView.setText(group.getName());
            holder.selfChange = true;
            holder.toggle.setChecked(group.isOn());
            holder.seekBar.setProgress(group.getLevel());
            holder.selfChange = false;

            holder.summaryLayout.setVisibility(View.VISIBLE);

            holder.summaryLayout.removeAllViews();
            for (GroupDevice groupDevice : group.getGroupDeviceList()) {
                View.inflate(getActivity(), R.layout.device_summary, holder.summaryLayout);
                DeviceSummary textView = (DeviceSummary) holder.summaryLayout.getChildAt(holder.summaryLayout.getChildCount()-1);

                if (groupDevice.getDevice() != null) {
                    textView.setText(groupDevice.getDevice().getName().substring(0, 1));
                }
                textView.setOn(false);
            }
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            boolean selfChange;
            int position;
            TextView textView;
            SwitchCompat toggle;
            SeekBar seekBar;
            ImageButton settingsButton;
            ViewGroup summaryLayout;

            Holder(View view) {
                view.setTag(this);
                textView = (TextView) view.findViewById(R.id.text);
                toggle = (SwitchCompat) view.findViewById(R.id.toggle);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                summaryLayout = (ViewGroup) view.findViewById(R.id.summary_layout);
                settingsButton.setOnClickListener(this);
                toggle.setOnCheckedChangeListener(this);
                seekBar.setOnSeekBarChangeListener(this);
                seekBar.setMax(255);
            }

            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                if (selfChange) {
                    return;
                }
                Group group = getItem(position);
                group.setOn(b);
                Syncro.getInstance().syncDevices();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (selfChange) {
                    return;
                }
                Group group = getItem(position);
                group.setLevel(seekBar.getProgress());
                Syncro.getInstance().syncDevices();
            }

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.group_item, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove:
                        Group group = getItem(position);
                        group.setState(BlinkApp.STATE_REMOVED);
                        group.update();
                        Syncro.getInstance().syncDevices();
                        break;

                    case R.id.action_edit:
                        Intent intent = getAddEditIntent(getItem(position));
                        startActivityForResult(intent, REQUEST_EDIT);
                        break;

                    case R.id.action_sync:
                        group = getItem(position);
                        group.setState(BlinkApp.STATE_UPDATED);
                        group.setOn(group.isOn());
                        group.setLevel(group.getLevel());
                        group.update();
                        Syncro.getInstance().syncDevices();
                        break;

                    case R.id.action_write_nfc:
                        group = getItem(position);
                        NfcUtils.stageWrite(getActivity(), group.toNfc());
                        break;
                }
                return false;
            }
        }
    }
}
