package com.mcssoft.racemeetings2.utility;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import com.mcssoft.racemeetings2.interfaces.IAsyncResult;

/**
 * Utility class - generic async task used for getting data via http.
 * Results are returned via the IAsyncResult interface.
 */
public class DownloadData extends AsyncTask<String,Void,String> {

    /**
     * Constructor.
     * @param context The app cpntext.
     * @param url The url for the http operation.
     * @param message A message for the progress dialog.
     * @param output Indicator as to where to direct the results returned.
     */
    public DownloadData(Context context, URL url, String message, String output) {
        this.context = context;
        this.message = message;
        this.url = url;
        this.output = output;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String theResult = null;
        try {
            HttpWrapper sw = new HttpWrapper(url);
            theResult = sw.remoteRequest();
        }
        catch (Exception ex) {
            Log.d("", ex.getMessage());
        }
        finally {
            return theResult;
        }
    }

    /*
      Runs on UI thread after doInBackground().
     */
    @Override
    protected void onPostExecute(String theResult) {
//        super.onPostExecute(theResult);
        progressDialog.dismiss();
        asyncResult.result(output, theResult);
    }

    public IAsyncResult asyncResult = null;

    private URL url;
    private String message;
    private String output;  // indicator as to where to direct the results returned.
    private Context context;
    private ProgressDialog progressDialog;
}
