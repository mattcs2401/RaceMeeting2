package com.mcssoft.racemeetings2.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.fragment.RacesFragment;
import com.mcssoft.racemeetings2.network.DownloadRequest;
import com.mcssoft.racemeetings2.network.DownloadRequestQueue;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.Url;

public class RacesActivity extends AppCompatActivity
        implements Response.ErrorListener, Response.Listener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseUI();
        dbOper = new DatabaseOperations(this);

        Bundle bundle = getIntent().getExtras();
        int dbRowId = bundle.getInt(Resources.getInstance()
                .getString(R.string.meetings_db_rowid_key));

        if (!checkRaceExists(dbRowId)) {
            String[] details = getMeetingCodeAndDate(dbRowId);
            Url url = new Url();
            String[] dateComp = url.getDateComponents(details[1]);
            String uri = url.createMeetingUrl(dateComp, details[0]);

            DownloadRequest dlReq = new DownloadRequest(Request.Method.GET, uri, this, this, this,
                    SchemaConstants.RACES_TABLE);
            DownloadRequestQueue.getInstance().addToRequestQueue(dlReq);
        } else {
            // Race info for thr selected meeting does exist.

        }
    }

    @Override
    public void onResponse(Object response) {
        // Will only return here if a download wasn't required, i.e. records already existed.
        String bp = "";
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO - some sort of generic error dialog ? with option to try again ?
        String bp = "";
    }

    private String[] getMeetingCodeAndDate(int dbRowId) {
        Cursor cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_DATE, SchemaConstants.MEETING_CODE},
                SchemaConstants.WHERE_MEETING_ROWID, new String[] {Integer.toString(dbRowId)});
        cursor.moveToFirst();
        String[] details = new String[2];
        details[0] = cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_CODE));
        details[1] = cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_DATE));
        return details;
    }

    private boolean checkRaceExists(int dbRowId) {
        String meetingId = getMeetingId(dbRowId);
        return dbOper.checkRecordExists(SchemaConstants.RACES_TABLE,
                                        SchemaConstants.RACE_MEETING_ID, meetingId);
    }

    private String getMeetingId(int dbRowId) {
        Cursor cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_ID}, SchemaConstants.WHERE_MEETING_ROWID,
                new String[] {Integer.toString(dbRowId)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_ID));
    }

    private void setBaseUI() {
        setContentView(R.layout.content_view_activity_races);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    private void loadFragment(String meetingId) {
        String fragment_tag = Resources.getInstance().getString(R.string.races_fragment_tag);
        Bundle bundle = new Bundle();
        bundle.putString(Resources.getInstance().getString(R.string.races_meeting_id_key), meetingId);

        RacesFragment racesFragment = new RacesFragment();
        racesFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.id_content_races, racesFragment, fragment_tag)
                .addToBackStack(fragment_tag)
                .commit();
    }

    private DatabaseOperations dbOper;

}
