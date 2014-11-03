package com.nashlincoln.blink.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.content.SceneLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.model.Scene;
import com.nashlincoln.blink.model.SceneDevice;
import com.nashlincoln.blink.nfc.NfcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class SceneListFragment extends Fragment {
    private static final String TAG = "SceneListFragment";
    private static final String ADD_FRAG = "add_frag";
    private ListView mListView;
    private SceneAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(inflater.getContext());
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SceneAdapter();
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            DialogFragment fragment =
                    (DialogFragment) Fragment.instantiate(getActivity(), AddSceneDialogFragment.class.getName());

            fragment.show(getFragmentManager(), ADD_FRAG);
            Log.d(TAG, "add");
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    class SceneAdapter extends BaseAdapter {

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
        }

        private void bindView(int position, Holder holder) {
            SceneDevice sceneDevice = (SceneDevice) getItem(position);
            holder.position = position;
            holder.textView.setText(sceneDevice.getDevice().getName());
            holder.selfChange = true;
            holder.toggle.setChecked(sceneDevice.isOn());
            holder.seekBar.setProgress(sceneDevice.getLevel());
            holder.selfChange = false;
        }

        public void setScenes(List<Scene> scenes) {
            sceneList.clear();
            for (Scene scene : scenes) {
                sceneList.add(scene);
                sceneList.addAll(scene.getSceneDeviceList());
            }

            notifyDataSetChanged();
        }

        class SceneHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            public int position;
            TextView nameView;
            ImageButton applyButton;
            ImageButton settingsButton;

            SceneHolder(View view) {
                view.setTag(this);
                nameView = (TextView) view.findViewById(R.id.text);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                applyButton = (ImageButton) view.findViewById(R.id.button_apply);
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
                switch (item.getItemId()) {
                    case R.id.action_remove:
                        Scene scene = (Scene) getItem(position);
                        scene.deleteWithReferences();
                        break;

                    case R.id.action_sync:
                        scene = (Scene) getItem(position);
                        scene.updateDevices();
                        Syncro.getInstance().syncDevices();
                        break;

                    case R.id.action_write_nfc:
                        scene = (Scene) getItem(position);
                        NfcUtils.stageWrite(getActivity(), scene.toNfc());
                        break;
                }
                return false;
            }
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
            boolean selfChange;
            int position;
            TextView textView;
            SwitchCompat toggle;
            SeekBar seekBar;
            ImageButton settingsButton;

            Holder(View view) {
                view.setTag(this);
                textView = (TextView) view.findViewById(R.id.text);
                toggle = (SwitchCompat) view.findViewById(R.id.toggle);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                settingsButton.setVisibility(View.GONE);
//                settingsButton.setOnClickListener(this);
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
