package com.mcssoft.racemeetings2.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IWriteResult;
import com.mcssoft.racemeetings2.model.Meeting;

import java.util.List;

/**
 * Utility class to write a list of one or more objects into the respective table.
 */
public class WriteResult extends AsyncTask<List,Void,String> {
    /**
     * Constructor.
     *
     * @param context   The app cpntext.
     * @param message   A message for the progress dialog.
     * @param input     The data to process.
     * @param inputType An indicator of what the inputList is associated with.
     */
    public WriteResult(Context context, String message, List input, String inputType) {
        this.context = context;
        this.message = message;
        this.inputList = input;
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
            switch (inputType) {
                case SchemaConstants.MEETINGS_TABLE:
                    checkOrUpdateMeetings();
                    break;
                case SchemaConstants.RACES_TABLE:
                    checkOrUpdateRaces();
                    break;
            }
        } catch (Exception ex) {
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

    private void checkOrUpdateMeetings() {
        DatabaseOperations dbOper = new DatabaseOperations(context);
        for(Object object : inputList) {
            Meeting meeting = ((Meeting) object);
            String meetingId = meeting.getMeetingId();
            if(!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE, SchemaConstants.MEETING_ID,
                    meetingId)) {
                dbOper.insertMeetingRecord(meeting);
            }
        }
    }

    private void checkOrUpdateRaces() {
        // TBA
    }


    public IWriteResult writeResult = null;

    private List inputList;
    private String inputType;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}