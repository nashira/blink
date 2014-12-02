package com.nashlincoln.blink.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.SceneLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.model.DaoSession;
import com.nashlincoln.blink.model.Scene;
import com.nashlincoln.blink.model.SceneDevice;
import com.nashlincoln.blink.nfc.NfcUtils;
import com.nashlincoln.blink.widget.DeviceSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class SceneListFragment extends BlinkListFragment {
    private static final String TAG = "SceneListFragment";
    private static final int REQUEST_EDIT = 123;
    private static final int REQUEST_ADD = 124;
    private SceneAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SceneAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        mListView.setDivider(null);
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    protected void onFabClick(View view) {
        Intent intent = getAddEditIntent(null);
        startActivityForResult(intent, REQUEST_ADD);
    }

    private LoaderManager.LoaderCallbacks<List<Scene>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Scene>>() {
        @Override
        public Loader<List<Scene>> onCreateLoader(int id, Bundle args) {
            return new SceneLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Scene>> loader, List<Scene> data) {
            mAdapter.setScenes(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Scene>> loader) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "" + requestCode + " " + resultCode);
        final DaoSession daoSession = BlinkApp.getDaoSession();

        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            final String name = data.getStringExtra(BlinkApp.EXTRA_NAME);
            final long[] ids = data.getLongArrayExtra(BlinkApp.EXTRA_DEVICE_IDS);
            final long id = data.getLongExtra(BlinkApp.EXTRA_ID, -1);

            if (ids == null || ids.length < 1) {
                return;
            }

            daoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    Scene scene = daoSession.getSceneDao().load(id);
                    scene.setName(name);
                    scene.setDeviceIds(ids);
                    scene.update();
                    scene.resetSceneDeviceList();
                    Event.broadcast(Scene.KEY);
                }
            });
        } else if (requestCode == REQUEST_ADD && resultCode == Activity.RESULT_OK && data != null) {
            final String name = data.getStringExtra(BlinkApp.EXTRA_NAME);
            final long[] ids = data.getLongArrayExtra(BlinkApp.EXTRA_DEVICE_IDS);

            if (ids == null || ids.length < 1) {
                return;
            }

            daoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    Scene scene = new Scene();
                    scene.setName(name);
                    daoSession.getSceneDao().insert(scene);
                    for (long id : ids) {
                        scene.addDevice(id);
                    }
                    scene.resetSceneDeviceList();
                    Event.broadcast(Scene.KEY);
                }
            });
        }
    }

    private Intent getAddEditIntent(Scene scene) {
        Intent intent = new Intent(getActivity(), EditListActivity.class);
        if (scene != null) {
            intent.putExtra(BlinkApp.EXTRA_ID, scene.getId());
            intent.putExtra(BlinkApp.EXTRA_NAME, scene.getName());
            long[] deviceIds = new long[scene.getSceneDeviceList().size()];
            int index = 0;
            for (SceneDevice sceneDevice : scene.getSceneDeviceList()) {
                deviceIds[index++] = sceneDevice.getDeviceId();
            }

            intent.putExtra(BlinkApp.EXTRA_DEVICE_IDS, deviceIds);
        }
        return intent;
    }

    class SceneAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private List<Scene> sceneListRaw = new ArrayList<>();

        private List<Object> sceneList = new ArrayList<>();

        public List<Object> getSceneList() {
            return sceneList;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Scene ? 0 : 1;
        }

        @Override
        public int getCount() {
            return sceneList.size();
        }

        @Override
        public Object getItem(int position) {
            return sceneList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (getItemViewType(position) == 1) {
                Holder holder;
                if (convertView == null) {
                    convertView = View.inflate(getActivity(), R.layout.device_item, null);
                    holder = new Holder(convertView);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                bindView(position, holder);

            } else {
                SceneHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(getActivity(), R.layout.scene_item, null);
                    holder = new SceneHolder(convertView);
                } else {
                    holder = (SceneHolder) convertView.getTag();
                }
                bindSceneView(position, holder);
            }

            return convertView;
        }

        private void bindSceneView(int position, SceneHolder holder) {
            Scene scene = (Scene) getItem(position);
            holder.position = position;
            holder.nameView.setText(scene.getName());
            if (scene.isExpanded()) {
                holder.view.setBackgroundResource(0);
                holder.summaryLayout.setVisibility(View.GONE);
            } else {
                holder.view.setBackgroundResource(getCount() == position + 1 ? 0 : R.drawable.line_bottom);
                holder.summaryLayout.setVisibility(View.VISIBLE);

                holder.summaryLayout.removeAllViews();
                for (SceneDevice sceneDevice : scene.getSceneDeviceList()) {
                    View.inflate(getActivity(), R.layout.device_summary, holder.summaryLayout);
                    DeviceSummary textView = (DeviceSummary) holder.summaryLayout.getChildAt(holder.summaryLayout.getChildCount()-1);
                    textView.setText(sceneDevice.getDevice().getName().substring(0, 1));
                    textView.setOn(sceneDevice.isOn());
                    textView.setLevel(sceneDevice.getLevel() / 255f);
                }
            }
        }

        private void bindView(int position, Holder holder) {
            SceneDevice sceneDevice = (SceneDevice) getItem(position);
            holder.position = position;
            holder.textView.setText(sceneDevice.getDevice().getName());
            holder.selfChange = true;
            holder.toggle.setChecked(sceneDevice.isOn());
            holder.seekBar.setProgress(sceneDevice.getLevel());
            holder.selfChange = false;
//            holder.view.setScaleY(0);
//            holder.view.animate().scaleY(1);

            if (getCount() > position + 1 && getItemViewType(position + 1) != 1) {
                holder.view.setBackgroundResource(R.drawable.line_bottom);
            } else {
                holder.view.setBackgroundResource(0);
            }
        }

        public void setScenes(List<Scene> scenes) {
            sceneListRaw = scenes;
            updateSceneList();
        }

        private void updateSceneList() {
            sceneList.clear();
            for (Scene scene : sceneListRaw) {
                sceneList.add(scene);
                if (scene.isExpanded()) {
                    sceneList.addAll(scene.getSceneDeviceList());
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getItemViewType(position) == 0) {
                Scene scene = (Scene) getItem(position);
                scene.setExpanded(!scene.isExpanded());
                updateSceneList();
            }
        }

        class SceneHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            public int position;
            View view;
            TextView nameView;
            ImageButton applyButton;
            ImageButton settingsButton;
            ViewGroup summaryLayout;

            SceneHolder(View view) {
                view.setTag(this);
                this.view = view;
                nameView = (TextView) view.findViewById(R.id.text);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                applyButton = (ImageButton) view.findViewById(R.id.button_apply);
                summaryLayout = (ViewGroup) view.findViewById(R.id.summary_layout);
                settingsButton.setOnClickListener(this);
                applyButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_settings:
                        PopupMenu popup = new PopupMenu(getActivity(), v);
                        popup.getMenuInflater().inflate(R.menu.group_item, popup.getMenu());
                        popup.setOnMenuItemClickListener(this);
                        popup.show();
                        break;

                    case R.id.button_apply:
                        Scene scene = (Scene) getItem(position);
                        scene.updateDevices();
                        Syncro.getInstance().syncDevices();
                        break;
                }
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Scene scene = (Scene) getItem(position);

                switch (item.getItemId()) {
                    case R.id.action_remove:
                        scene.deleteWithReferences();
                        break;

                    case R.id.action_edit:
                        Intent intent = getAddEditIntent(scene);
                        startActivityForResult(intent, REQUEST_EDIT);
                        break;

                    case R.id.action_sync:
                        scene.updateDevices();
                        Syncro.getInstance().syncDevices();
                        break;

                    case R.id.action_write_nfc:
                        NfcUtils.stageWrite(getActivity(), scene.toNfc());
                        break;
                }

                return false;
            }
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
            boolean selfChange;
            int position;
            View view;
            TextView textView;
            SwitchCompat toggle;
            SeekBar seekBar;
            ImageButton settingsButton;

            Holder(View view) {
                view.setTag(this);
                this.view = view;
                textView = (TextView) view.findViewById(R.id.text);
                toggle = (SwitchCompat) view.findViewById(R.id.toggle);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                settingsButton.setVisibility(View.GONE);
                toggle.setOnCheckedChangeListener(this);
                seekBar.setOnSeekBarChangeListener(this);
                seekBar.setMax(255);
            }

            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                if (selfChange) {
                    return;
                }
                SceneDevice sceneDevice = (SceneDevice) getItem(position);
                sceneDevice.setOn(b);
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
                SceneDevice sceneDevice = (SceneDevice) getItem(position);
                sceneDevice.setLevel(seekBar.getProgress());
            }
        }
    }
}
