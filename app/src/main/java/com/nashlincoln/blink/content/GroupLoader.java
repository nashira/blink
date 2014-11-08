package com.nashlincoln.blink.content;

import android.content.Context;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.model.Group;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class GroupLoader extends ModelLoader<List<Group>> {

    public GroupLoader(Context context) {
        super(context);
    }

    @Override
    public List<Group> fetch() {
        return BlinkApp.getDaoSession().getGroupDao().loadAll();
    }

    @Override
    public String getKey() {
        return Group.KEY;
    }
}
