package com.mcssoft.racemeetings2.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Static wrapper for a Volley RequestQueue.
 */
public class DownloadRequestQueue {

    private DownloadRequestQueue(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized DownloadRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadRequestQueue(context);
        }
        return instance;
    }

    public static DownloadRequestQueue getInstance() {
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }

    public void destroy() {
        requestQueue.cancelAll(null);
        context = null;
    }

    private Context context;
    private RequestQueue requestQueue;
    private static DownloadRequestQueue instance = null;

}