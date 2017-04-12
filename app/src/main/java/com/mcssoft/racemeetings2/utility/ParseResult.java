package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IParseResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class to parse information (as Xml) into a list of objects.
 * Results are returned via the IParseResult interface.
 */
public class ParseResult extends AsyncTask<String,Void,List> {
    /**
     * Constructor.
     * @param context The app cpntext.
     * @param message A message for the progress dialog.
     * @param input   The data to process.
     * @param output  An indicator of what the output is associated with.
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
                case SchemaConstants.MEETINGS_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.meetings_xml_tag), instream);
                    break;
                case SchemaConstants.RACES_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.races_xml_tag), instream);
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
        iResult.parseResult(output, theResult);
    }

    public IParseResult iResult = null;

    private String input;
    private String output;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}
