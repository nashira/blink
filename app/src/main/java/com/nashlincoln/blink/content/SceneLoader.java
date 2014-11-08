package com.nashlincoln.blink.content;

import android.content.Context;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.model.Scene;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class SceneLoader extends ModelLoader<List<Scene>> {

    public SceneLoader(Context context) {
        super(context);
    }

    @Override
    public List<Scene> fetch() {
        return BlinkApp.getDaoSession().getSceneDao().loadAll();
    }

    @Override
    public String getKey() {
        return Scene.KEY;
    }
}
