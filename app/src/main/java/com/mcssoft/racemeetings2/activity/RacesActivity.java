package com.mcssoft.racemeetings2.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.fragment.RacesFragment;
import com.mcssoft.racemeetings2.interfaces.IDownloadResult;
import com.mcssoft.racemeetings2.utility.Resources;

public class RacesActivity extends AppCompatActivity
        implements IDownloadResult {

    public void downloadResult(String table, String results) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        int dbRowId = bundle.getInt(Resources.getInstance()
                .getString(R.string.meetings_db_rowid_key));

        if(!checkRaceExists(dbRowId)) {
            // TODO - download race data and parse <Race> based on <Meeting MeetingCode...>
        }
        setBaseUI();
        String meetingId = "";
        loadFragment(meetingId);
    }


    private boolean checkRaceExists(int dbRowId) {
        DatabaseOperations dbOper = new DatabaseOperations(this);
        Cursor cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_ID},
                SchemaConstants.WHERE_MEETING_ROWID, new String[] {Integer.toString(dbRowId)});
        cursor.moveToFirst();
        String meetingId = cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_ID));

        return dbOper.checkRecordExists(SchemaConstants.RACES_TABLE,
                SchemaConstants.RACE_MEETING_ID, meetingId);
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



}
