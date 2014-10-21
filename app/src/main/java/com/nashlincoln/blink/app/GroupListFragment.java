package com.nashlincoln.blink.app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.content.GroupLoader;
import com.nashlincoln.blink.model.Syncro;
import com.nashlincoln.blink.model.Group;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class GroupListFragment extends Fragment {
    private ListView mListView;
    private GroupAdapter mAdapter;

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
            holder.checkBox.setChecked(group.isOn());
            holder.seekBar.setProgress(group.getLevel());
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
                Group group = getItem(position);
                group.setOn(b);
                Syncro.getInstance().syncGroups();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Group group = getItem(position);
                group.setLevel(seekBar.getProgress());
                Syncro.getInstance().syncGroups();
            }
        }
    }
}
