package com.mcssoft.racemeetings2.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.fragment.RacesFragment;
import com.mcssoft.racemeetings2.model.Race;
import com.mcssoft.racemeetings2.network.DownloadRequest;
import com.mcssoft.racemeetings2.network.DownloadRequestQueue;
import com.mcssoft.racemeetings2.utility.DateTime;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.Url;

import java.util.List;

public class RacesActivity extends AppCompatActivity
        implements Response.Listener,
                   Response.ErrorListener,
                   ImageView.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        int dbRowId = bundle.getInt(Resources.getInstance().getString(R.string.meetings_db_rowid_key));
        dbOper = new DatabaseOperations(this);
        meetingDetails = getMeetingCodeAndDate(dbRowId);

        setBaseUI();

        if (!checkRaceExists(dbRowId)) {
            DateTime dateTime = new DateTime();
            String[] dateComp = dateTime.getDateComponents(meetingDetails[1]);
            Url url = new Url();
            String uri = url.createMeetingUrl(dateComp, meetingDetails[0]);

            DownloadRequest dlReq = new DownloadRequest(Request.Method.GET, uri, this, this, this,
                    SchemaConstants.RACES_TABLE);
            dlReq.setTag("races_tag");
            DownloadRequestQueue.getInstance().addToRequestQueue(dlReq);
        } else {
            // Race info for thr selected meeting already exists.
            loadFragment(getMeetingId(dbRowId));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOper = null;
        DownloadRequestQueue.getInstance().destroy();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            finish();
        } else if (backStackEntryCount > 1) {
                getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        onBackPressed();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Volley return">
    /**
     * Volley download will return here.
     * @param response A list of Race objects.
     */
    @Override
    public void onResponse(Object response) {
        // TODO - is there a chance response will be null ?
        // Get the meeting id from the first object (will be the same for all).
        String meetingId = ((Race) ((List) response).get(0)).getMeetingId();
        loadFragment(meetingId);
    }

    /**
     * A Volley error condition will return here.
     * @param error The Volley error object.
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO - some sort of generic error dialog ? with option to try again ?
        String bp = "";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    /**
     * Get a Meeting's code and date values.
     * @param dbRowId The Meeting table's row id.
     * @return [0]-meeting code, [1]-meeting date (as YYYY/MM/DD).
     */
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

    /**
     * Check if a Race with the given meeting id exists.
     * @param dbRowId The Meeting table's row id.
     * @return True if exists.
     */
    private boolean checkRaceExists(int dbRowId) {
        return dbOper.checkRecordsExist(SchemaConstants.RACES_TABLE,
                                        SchemaConstants.RACE_MEETING_ID, getMeetingId(dbRowId));
    }

    /**
     * Get the Meeting's id.
     * @param dbRowId The Meeting table's row id.
     * @return The Meeting id.
     */
    private String getMeetingId(int dbRowId) {
        Cursor cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_ID}, SchemaConstants.WHERE_MEETING_ROWID,
                new String[] {Integer.toString(dbRowId)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_ID));
    }

    private void setBaseUI() {
        setContentView(R.layout.content_view_races);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar); //races_toolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.id_tv_toolbar);
        textView.setText("Races for " + meetingDetails[0] + " " + meetingDetails[1]);
        ImageView imageView = (ImageView) toolbar.findViewById(R.id.id_iv_toolbar);
        imageView.setImageResource(R.drawable.ic_arrow_back);
        imageView.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Load the Race fragment.
     * @param meetingId The associated Meeting id (as argument to fragment).
     */
    private void loadFragment(String meetingId) {
        String fragment_tag = Resources.getInstance().getString(R.string.races_fragment_tag);
        Bundle bundle = new Bundle();
        bundle.putString(Resources.getInstance().getString(R.string.races_meeting_id_key), meetingId);

        RacesFragment racesFragment = new RacesFragment();
        racesFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.id_races_container, racesFragment, fragment_tag)
                .addToBackStack(fragment_tag)
                .commit();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private String[] meetingDetails;   // meeting code and date.
    private DatabaseOperations dbOper;
    //</editor-fold>
}
