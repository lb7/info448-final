package edu.uw.lbaker7.localtravelapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static android.content.ContentValues.TAG;

/**
 * Created by Christa Joy Jaeger on 5/24/2017.
 */

public class PlacesRequestQueue {
    private static PlacesRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private PlacesRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        Log.v(TAG,"This happened 2");

    }

    public static synchronized PlacesRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlacesRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
