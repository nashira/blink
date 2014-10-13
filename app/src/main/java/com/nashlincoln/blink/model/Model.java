package com.nashlincoln.blink.model;

import com.nashlincoln.blink.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nash on 10/5/14.
 */
public abstract class Model {
    List<OnEvent> mObservers = new ArrayList<OnEvent>();

    public void addOnEvent(OnEvent onEvent) {
        addObserver(onEvent);
    }

    public void removeOnEvent(OnEvent onEvent) {
        deleteObserver(onEvent);
    }

    protected void event(Event event) {
        notifyObservers(event);
    }

    private void addObserver(OnEvent observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (this) {
            if (!mObservers.contains(observer))
                mObservers.add(observer);
        }
    }

    private synchronized void deleteObserver(OnEvent observer) {
        mObservers.remove(observer);
    }

    public void notifyObservers(Event data) {
        int size;
        OnEvent[] arrays = null;
        synchronized (this) {
            size = mObservers.size();
            arrays = new OnEvent[size];
            mObservers.toArray(arrays);
        }
        for (OnEvent observer : arrays) {
            observer.onEvent(this, data);
        }
    }

    public interface OnEvent {
        public abstract void onEvent(Model model, Event event);
    }
}
