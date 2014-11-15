package com.nashlincoln.blink.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nashlincoln.blink.R;

/**
 * Created by nash on 11/10/14.
 */
public class EditListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = Fragment.instantiate(this, EditListFragment.class.getName(), getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }
}
