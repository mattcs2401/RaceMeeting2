package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mcssoft.racemeetings2.interfaces.IResult;
import com.mcssoft.racemeetings2.model.Meeting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class to parse Meeting information (as Xml) into a list of Meeting objects.
 * Results are returned via the IResult interface.
 */
public class ParseResult extends AsyncTask<String,Void,List> {
    /**
     * Constructor.
     * @param context The app cpntext.
     * @param message A message for the progress dialog.
     * @param input   The data to process.
     * @param output  An indicator of what the out is.
     */
    public ParseResult(Context context, String message, String input, String output) {
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
    protected List doInBackground(String... params) {
        List theResult = null;
        InputStream instream = new ByteArrayInputStream(input.getBytes());
        XmlParser parser = new XmlParser();
        try {
            switch(output) {
                case "Meetings":
                    theResult = parser.parse("Meetings", instream);
                    break;
                case "Races":
                    theResult = parser.parse("Races", instream);
                    break;
            }
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
        iResult.result(output, theResult);
    }

    public IResult iResult = null;

    private String input;
    private String output;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}
