package com.nashlincoln.blink.content;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

/**
 * Created by nash on 8/28/14.
 */
public abstract class ModelLoader<T> extends AsyncTaskLoader<T> {
    private static final String TAG = "ModelLoader";
    private T mData;

    public ModelLoader(Context context) {
        super(context);
    }

    public abstract T fetch();

    @Override
    public T loadInBackground() {
//        Log.d(TAG, "loadInBackground");
        try {
            // check memory cache
            // check disk cache

            mData = fetch();
        } catch (Exception e) {
            Log.w(TAG, "exception in fetch: ", e);
        }

        return mData;
    }

    @Override public void deliverResult(T data) {
//        Log.d(TAG, "deliverResult: " + isReset() + " " + isStarted());
        if (isReset()) {
            if (data != null) {
                onReleaseResources(data);
            }
            return;
        }

        T oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
//        Log.d(TAG, "onStartLoading");
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
//        Log.d(TAG, "onStopLoading");
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(T stores) {
//        Log.d(TAG, "onCanceled");
        super.onCanceled(stores);

        onReleaseResources(stores);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
//        Log.d(TAG, "onReset");
        super.onReset();
        onStopLoading();

        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    protected void onReleaseResources(T data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
