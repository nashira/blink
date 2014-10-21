package com.nashlincoln.blink.event;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by nash on 10/5/14.
 */
public class Event {
//    public Type type;
//    public Status status;
//
//    public Event(Type type, Status status) {
//        this.type = type;
//        this.status = status;
//    }

    private static final Map<String, ObsImpl> sObservables = new HashMap<>();
    private static final String TAG = "Event";

    public static void observe(String key, Observer observer) {
        Log.d(TAG, "observe");
        if (!sObservables.containsKey(key)) {
            sObservables.put(key, new ObsImpl());
        }
        sObservables.get(key).addObserver(observer);
    }

    public static void ignore(String key, Observer observer) {
        Log.d(TAG, "ignore");
        if (sObservables.containsKey(key)) {
            sObservables.get(key).deleteObserver(observer);
        }
    }

    public static void notify(String key) {
        Log.d(TAG, "notify");
        if (sObservables.containsKey(key)) {
            sObservables.get(key).notifyObservers();
        }
    }

    private static class ObsImpl extends Observable {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }

        @Override
        public void notifyObservers(Object data) {
            setChanged();
            super.notifyObservers(data);
        }
    }
}
