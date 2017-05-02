package com.mcssoft.racemeetings2.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Request;
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
        List weather = null;                // additional weather info.
        XmlParser parser = new XmlParser();
        InputStream instream = new ByteArrayInputStream(response.data);

        try {
            // Parse the response into 'Meeting' or 'Race' objects.
            switch(output) {
                case SchemaConstants.MEETINGS_TABLE:
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.meetings_xml_tag), instream);
                    break;
                case SchemaConstants.RACES_TABLE:
                    weather = parser.parse("weather", instream);
                    theResult = parser.parse(Resources.getInstance()
                            .getString(R.string.races_xml_tag), instream);
                    break;
            }
            // Write the results to the database (if don't already exist).
            if (weather != null) {
                checkOrInsert(weather, SchemaConstants.MEETINGS_TABLE, true);
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
                for(Object object : theList) {
                    Meeting meeting = ((Meeting) object);
                    if(!dbOper.checkRecordExists(SchemaConstants.MEETINGS_TABLE,
                                                 SchemaConstants.MEETING_ID,
                                                 meeting.getMeetingId())) {
                        dbOper.insertMeetingRecord(meeting);
                    } else if(hasWeather) {
                        dbOper.updateMeetingRecordWeather(meeting);
                    }
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

    private String output;
    private Context context;
    private Response.Listener<List> listener;
    private Response.ErrorListener errorListener;
}
