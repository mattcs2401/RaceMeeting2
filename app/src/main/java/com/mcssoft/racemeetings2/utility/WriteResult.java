package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IWriteResult;

import java.util.List;

/**
 * Utility class to write a list of one or more objects into the respective table.
 */
public class WriteResult extends AsyncTask<List,Void,String> {

    /**
     * Constructor.
     * @param context The app cpntext.
     * @param message A message for the progress dialog.
     * @param input   The data to process.
     * @param inputType An indicator of what the input is associated with.
     */
    public WriteResult(Context context, String message, List input, String inputType) {
        this.context = context;
        this.message = message;
        this.input = input;
        this.inputType = inputType;
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
    protected String doInBackground(List... params) {
        String theResult = null;
        try {
            switch(inputType) {
                case SchemaConstants.MEETINGS_TABLE:
                    break;
                case SchemaConstants.RACES_TABLE:
                    break;
            }
        } catch(Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return theResult;
    }

    /*
      Runs on UI thread after doInBackground().
    */
    @Override
    protected void onPostExecute(String theResult) {
        progressDialog.dismiss();
        writeResult.writeResult(inputType, theResult);
    }

    public IWriteResult writeResult = null;

    private List input;
    private String inputType;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}
/*
    private void checkOrUpdateMeetings(List resultList) {
        DatabaseOperations dbOper = new DatabaseOperations(this);
        for(Object meeting : resultList) {
            //meetingIdList.add(((Meeting) meeting).getMeetingId());
            String meetingId = ((Meeting) meeting).getMeetingId();
            if(!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE, SchemaConstants.MEETING_ID,
                    meetingId)) {
                dbOper.insertMeetingRecord((Meeting) meeting);
                // TBA - insert meeting record.
                String bp = "";

            }
        }


        String bp = "";
    }
*/
