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
public class WriteResult extends AsyncTask<List,Void,Boolean> {
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
    protected Boolean doInBackground(List... params) {
        boolean theResult = false;
        try {
            switch (inputType) {
                case SchemaConstants.MEETINGS_TABLE:
                    theResult = checkOrUpdateMeetings();
                    break;
                case SchemaConstants.RACES_TABLE:
                    theResult = checkOrUpdateRaces();
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
    protected void onPostExecute(Boolean theResult) {
        progressDialog.dismiss();
        writeResult.writeResult(inputType, theResult);
    }

    private boolean checkOrUpdateMeetings() {
        boolean recordInserted = false;
        DatabaseOperations dbOper = new DatabaseOperations(context);
        for(Object object : inputList) {
            Meeting meeting = ((Meeting) object);
//            String meetingId = meeting.getMeetingId();
            if(!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE, SchemaConstants.MEETING_ID,
                    meeting.getMeetingId())) {
                dbOper.insertMeetingRecord(meeting);
                if(!recordInserted) {
                    recordInserted = true;}
            }
        }
        return recordInserted;
    }

    private boolean checkOrUpdateRaces() {
        // TBA
        return false;
    }


    public IWriteResult writeResult = null;

    private List inputList;
    private String inputType;
    private String message;
    private Context context;
    private ProgressDialog progressDialog;
}