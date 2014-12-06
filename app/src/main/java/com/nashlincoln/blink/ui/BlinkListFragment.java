package com.nashlincoln.blink.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nashlincoln.blink.R;

/**
 * Created by nash on 11/8/14.
 */
public class BlinkListFragment extends Fragment implements View.OnClickListener {
    protected ListView mListView;
    protected ImageButton mFabButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mFabButton = (ImageButton) view.findViewById(R.id.fab);

        View footer = inflater.inflate(R.layout.list_footer, null);
        mListView.addFooterView(footer, null, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFabButton.setOnClickListener(this);
    }

    protected void onFabClick(View view) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                onFabClick(v);
        }
    }
}
