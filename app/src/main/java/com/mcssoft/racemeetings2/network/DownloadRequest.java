package com.mcssoft.racemeetings2.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Request;

public class DownloadRequest<T> extends Request<T> {


    public DownloadRequest(String url, Response.ErrorListener listener) {
        super(url, listener);
    }

    public DownloadRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        return null;
//        return Response.success()
    }

    @Override
    protected void deliverResponse(T response) {
        String bp = "";
    }
}
