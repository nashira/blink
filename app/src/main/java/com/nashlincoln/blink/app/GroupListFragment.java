package com.nashlincoln.blink.app;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.content.GroupLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.model.Group;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class GroupListFragment extends Fragment {
    private static final String TAG = "GroupListFragment";
    private static final String ADD_FRAG = "add_frag";
    private ListView mListView;
    private GroupAdapter mAdapter;

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
        mAdapter = new GroupAdapter(getActivity());
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
                    (DialogFragment) Fragment.instantiate(getActivity(), AddGroupDialogFragment.class.getName());

            fragment.show(getFragmentManager(), ADD_FRAG);
            Log.d(TAG, "add");
        }
        return super.onOptionsItemSelected(item);
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
                convertView = View.inflate(getContext(), R.layout.device_item, null);
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
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
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
                        group.deleteWithReferences();
                        break;

                    case R.id.action_sync:
                        group = getItem(position);
                        group.updateDevices();
                        Syncro.getInstance().syncDevices();
                        break;
                }
                return false;
            }
        }
    }
}
