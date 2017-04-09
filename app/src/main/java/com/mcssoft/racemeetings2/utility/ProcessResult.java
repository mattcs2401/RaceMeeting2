package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mcssoft.racemeetings2.interfaces.IProcessResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ProcessResult extends AsyncTask<String,Void,String> {
    /**
     * Constructor.
     * @param context The app cpntext.
     * @param message A message for the progress dialog.
     * @param input   The data to process.
     * @param output Indicator as to where to direct the results returned.
     */
    public ProcessResult(Context context, String message, String input, String output) {
        this.context = context;
        this.message = message;
        this.input = input;
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
        InputStream instream = new ByteArrayInputStream(input.getBytes());
        XmlParser parser = new XmlParser();
        try {
            parser.parse(instream);

        } catch(Exception ex) {

        } finally {
            return null;
        }
    }

    /*
    Runs on UI thread after doInBackground().
    */
    @Override
    protected void onPostExecute(String theResult) {
//        super.onPostExecute(theResult);
        progressDialog.dismiss();
        processResult.processResult(output, theResult);
    }

    public IProcessResult processResult = null;

    private String input;
    private String output;  // indicator as to where to direct the results returned.
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}
