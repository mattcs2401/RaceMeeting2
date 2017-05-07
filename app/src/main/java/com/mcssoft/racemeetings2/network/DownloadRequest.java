package com.mcssoft.racemeetings2.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.model.Meeting;
import com.mcssoft.racemeetings2.model.Race;
import com.mcssoft.racemeetings2.model.Runner;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.XmlParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Custom Volley.Request<T> class
 * @param <T>
 */
public class DownloadRequest<T> extends Request<List> {

    public DownloadRequest(int method, String url, Context context, Response.Listener listener,
                           Response.ErrorListener errorListener, String tableName) {
        super(method, url, errorListener);
        this.context = context;
        this.tableName = tableName;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    // From the doco, runs on a background worker thread.
    @Override
    protected Response<List> parseNetworkResponse(NetworkResponse response) {
        List theResult = null;         // main list of result objects from parsing the Xml.
        XmlParser parser = null;
        List meetingWeather = null;    // additional weather info.

        InputStream instream = new ByteArrayInputStream(response.data);

        try {
            parser = new XmlParser(instream);
            // Parse the response into Meeting, Race or Runner objects.
            switch(tableName) {
                case SchemaConstants.MEETINGS_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.meetings_xml_tag));
                    break;
                case SchemaConstants.RACES_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.races_xml_tag));
                    // Meeting weather info is 1st element in list.
                    meetingWeather = (List) theResult.get(0);
                    // Only want Race objects.
                    theResult = theResult.subList(1, theResult.size());
                    break;
                case SchemaConstants.RUNNERS_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.runners_xml_tag));
                    break;
            }
            // Write the results to the database (if don't already exist).
            if (meetingWeather != null) {
                // put weather info into the Meeting record.
                checkOrInsert(meetingWeather, SchemaConstants.MEETINGS_TABLE, true);
                // put meeting id into Race records (meeting id is part of weather info).
                theResult = mergeMeetingId((String) meetingWeather.get(0), theResult);
            }
            checkOrInsert(theResult, tableName, false);

        } catch(Exception ex) {
            Log.d(this.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            return Response.success(theResult, null);
        }
    }

    @Override
    protected void deliverResponse(List response) {
        // TODO - if the response is already written to the database, do we need the whole list?.
        // This just a callback, so maybe just use the list to indicate job done,
        // some sort of message ?.
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    /**
     * Insert objects into the database (if don't already exist).
     * @param theList The list of objects (Meeting or Race or Runner).
     * @param output An indicator as to what table to write to.
     * @param hasWeather An indicator that only updating one Meeting's weather related info.
     */
    private void checkOrInsert(List theList, String output, boolean hasWeather) {
        DatabaseOperations dbOper = new DatabaseOperations(context);
        switch (output) {
            case SchemaConstants.MEETINGS_TABLE:
                if(!hasWeather) {
                    checkOrInsertMeetings(theList); //Meeting objects (less weather info).
                } else {
                    // Update existing Meeting record with weather info.
                    dbOper.updateMeetingRecordWeather(createMeetingWeather(theList));
                }
                break;
            case SchemaConstants.RACES_TABLE:
                checkOrInsertRaces(theList);
                break;
            case SchemaConstants.RUNNERS_TABLE:
                checkOrInsertRunners(theList);
                break;
        }
    }

    /**
     * Insert Meeting objects into the database (if don't already exist).
     * @param list The list of Meeting objects.
     */
    private void checkOrInsertMeetings(List list) {
        Meeting meeting = null;
        DatabaseOperations dbOper = new DatabaseOperations(context);

        for(Object object : list) {
            // this is a new Meeting record.
            meeting = ((Meeting) object);
            if (!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE,
                    SchemaConstants.MEETING_ID, meeting.getMeetingId())) {
                dbOper.insertMeetingRecord(meeting);
            }
        }
    }

    /**
     * Insert Race objects into the database (if don't already exist).
     * @param list The list of Race objects.
     */
    private void checkOrInsertRaces(List list) {
        DatabaseOperations dbOper = new DatabaseOperations(context);
        for(Object object : list) {
            Race race = ((Race) object);
            if(!dbOper.checkRecordExists(SchemaConstants.RACES_TABLE,
                    SchemaConstants.RACE_MEETING_ID, race.getMeetingId())) {
                dbOper.insertRaceRecord(race);
            }
        }
    }

    /**
     * Insert Runner objects into the database (if don't already exist).
     * @param list The list of Runner objects.
     */
    private void checkOrInsertRunners(List list) {
        DatabaseOperations dbOper = new DatabaseOperations(context);
        for(Object object : list) {
            Runner runner = ((Runner) object);
            // TODO - insert Runner objects.
//            if(!dbOper.checkRecordExists(SchemaConstants.RUNNERS_TABLE,
//                    SchemaConstants.RACE_MEETING_ID, race.getMeetingId())) {
//                dbOper.insertRaceRecord(race);
//            }
        }
    }

    /**
     * Write Meeting identifier into Race objects.
     * @param meetingId The Meeting identifier.
     * @param theResult The list of Race objects.
     * @return The updated list of Race objects.
     */
    private List mergeMeetingId(String meetingId, List theResult) {
        for(Object object : theResult) {
            Race race = (Race) object;
            race.setMeetingId(meetingId);
        }
        return theResult;
    }

    /**
     * Create a Meeting object that only contains weather info.
     * @param theList A list of the info to use.
     * @return A Meeting object.
     */
    private Meeting createMeetingWeather(List theList) {
        Meeting meeting = new Meeting();
        //[0]-meeting id, [1]-track desc, [2]-track rating, [3]-weather desc.
        meeting.setMeetingId((String) theList.get(0));
        meeting.setTrackDescription((String) theList.get(1));
        meeting.setTrackRating((String) theList.get(2));
        meeting.setTrackWeather((String) theList.get(3));
        return meeting;
    }
    //</editor-fold>

    private String tableName;                         // the affected table.
    private Context context;                          // context for database operations.
    private Response.Listener<List> listener;         // non-error listener callback.
    private Response.ErrorListener errorListener;     // error listener callback.
}
