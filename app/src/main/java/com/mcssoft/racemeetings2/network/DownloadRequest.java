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
                           Response.ErrorListener errorListener, String output) {
        super(method, url, errorListener);
        this.context = context;
        this.output = output;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    // From the doco, runs on a background worker thread.
    @Override
    protected Response<List> parseNetworkResponse(NetworkResponse response) {
        List theResult = null;
        List meetingWeather = null;                // additional weather info.
        InputStream instream = new ByteArrayInputStream(response.data);
        XmlParser parser = null;

        try {
            parser = new XmlParser(instream);
        } catch(Exception ex) {
            Log.d(this.getClass().getCanonicalName(), ex.getMessage());
        }

        try {
            // Parse the response into 'Meeting' or 'Race' objects.
            switch(output) {
                case SchemaConstants.MEETINGS_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.meetings_xml_tag));
                    break;
                case SchemaConstants.RACES_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.races_xml_tag));
                    // Meeting weather info is 1st element, 2nd element contains Race objects.
                    meetingWeather = (List) theResult.get(0);
                    // Only want Race objects.
                    theResult = theResult.subList(1, theResult.size());
                    break;
            }
            // Write the results to the database (if don't already exist).
            if (meetingWeather != null) {
                // put weather info into the Meeting record.
                checkOrInsert(meetingWeather, SchemaConstants.MEETINGS_TABLE, true);
                // put meeting id into Race records (meeting id is part of weather info).
                theResult = mergeMeetingId((String) meetingWeather.get(0), theResult);
            }
            checkOrInsert(theResult, output, false);

        } catch(Exception ex) {
            Log.d(this.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            return Response.success(theResult, null);
        }
    }

    @Override
    protected void deliverResponse(List response) {
        // TODO - if the response is already written to the database, do we need the whole list?.
        // This just a callback, so maybe just use the list to indicate job done.
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }

    private boolean checkOrInsert(List theList, String output, boolean hasWeather) {
        DatabaseOperations dbOper = new DatabaseOperations(context);
        switch (output) {
            case SchemaConstants.MEETINGS_TABLE:
                Meeting meeting = null;
                if(!hasWeather) {
                    for(Object object : theList) {
                        // this is a new Meeting record.
                        meeting = ((Meeting) object);
                        if (!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE,
                                SchemaConstants.MEETING_ID, meeting.getMeetingId())) {
                            dbOper.insertMeetingRecord(meeting);
                        }
                    }
                } else {
                    // Update existing Meeting record with weather info.
                    meeting = createMeetingWeather(theList);
                    dbOper.updateMeetingRecordWeather(meeting);
                }
                break;
            case SchemaConstants.RACES_TABLE:
                for(Object object : theList) {
                    Race race = ((Race) object);
                    if(!dbOper.checkRecordExists(SchemaConstants.RACES_TABLE,
                            SchemaConstants.RACE_MEETING_ID, race.getMeetingId())) {
                        dbOper.insertRaceRecord(race);
                    }
                }
                break;
        }
        return dbOper.checkTableRowCount(output);
    }

    private List mergeMeetingId(String meetingId, List theResult) {
        for(Object object : theResult) {
            Race race = (Race) object;
            race.setMeetingId(meetingId);
        }
        return theResult;
    }

    private Meeting createMeetingWeather(List theList) {
        Meeting meeting = new Meeting();
        //[0]-meeting id, [1]-track desc, [2]-track rating, [3]-weather desc.
        meeting.setMeetingId((String) theList.get(0));
        meeting.setTrackDescription((String) theList.get(1));
        meeting.setTrackRating((String) theList.get(2));
        meeting.setTrackWeather((String) theList.get(3));
        return meeting;
    }

    private String output;
    private Context context;
    private Response.Listener<List> listener;
    private Response.ErrorListener errorListener;
}
