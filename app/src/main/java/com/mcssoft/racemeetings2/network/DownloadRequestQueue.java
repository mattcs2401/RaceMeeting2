package com.mcssoft.racemeetings2.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DownloadRequestQueue {

    private DownloadRequestQueue(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
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

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        //getRequestQueue().add(req);
        requestQueue.add(request);
    }

    public void destroy() {
        requestQueue.cancelAll(null);
        context = null;
    }

    private static Context context;
    private RequestQueue requestQueue;
    private static DownloadRequestQueue instance = null;

}