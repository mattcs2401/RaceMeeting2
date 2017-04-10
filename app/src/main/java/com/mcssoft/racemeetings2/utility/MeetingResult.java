package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mcssoft.racemeetings2.interfaces.IMeetingResult;
import com.mcssoft.racemeetings2.model.Meeting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class to parse Meeting information (as Xml) into a list of Meeting objects.
 * Results are returned via the IMeetingResult interface.
 */
public class MeetingResult extends AsyncTask<String,Void,List> {
    /**
     * Constructor.
     * @param context The app cpntext.
     * @param message A message for the progress dialog.
     * @param input   The data to process.
     */
    public MeetingResult(Context context, String message, String input) {
        this.context = context;
        this.message = message;
        this.input = input;
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
    protected List doInBackground(String... params) {
        List<Meeting> theResult = null;
        InputStream instream = new ByteArrayInputStream(input.getBytes());
        XmlParser parser = new XmlParser();
        try {
            theResult = parser.parse(instream);
        } catch(Exception ex) {
             Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            return theResult;
        }
    }

    /*
    Runs on UI thread after doInBackground().
    */
    @Override
    protected void onPostExecute(List theResult) {
        progressDialog.dismiss();
        processResult.meetingResult(theResult);
    }

    public IMeetingResult processResult = null;

    private String input;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}
