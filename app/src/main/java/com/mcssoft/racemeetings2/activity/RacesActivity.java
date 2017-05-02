package com.mcssoft.racemeetings2.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.fragment.RacesFragment;
import com.mcssoft.racemeetings2.utility.RaceDate;
import com.mcssoft.racemeetings2.utility.Resources;

public class RacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseUI();

        Bundle bundle = getIntent().getExtras();
        int dbRowId = bundle.getInt(Resources.getInstance()
                .getString(R.string.meetings_db_rowid_key));

        if (!checkRaceExists(dbRowId)) {
            downloadRawMeetingData(dbRowId);
        }
    }

    private void downloadRawMeetingData(int dbRowId) {
        String msg = "";
        String[] details = getMeetingCodeAndDate(dbRowId);
        RaceDate rd = new RaceDate();
        String[] dateComp = rd.getDateComponents(details[1]);
        try {
            msg = createDownloadMessage(dateComp, details[0]);
//            URL url = new URL(createMeetingUrl(dateComp, details[0]));

//            DownloadData dld = new DownloadData(this, url, msg, SchemaConstants.RACES_TABLE);
//            dld.downloadResult = this;
//            dld.execute();
        } catch(Exception ex) {
            // TBA
        }
    }

    private String createMeetingUrl(String[] date, String code) {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(date[0])
                .appendPath(date[1])
                .appendPath(date[2])
                .appendPath(code + ".xml");
        builder.build();
        return builder.toString();
    }

    private String[] getMeetingCodeAndDate(int dbRowId) {
        DatabaseOperations dbOper = new DatabaseOperations(this);
        Cursor cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_DATE, SchemaConstants.MEETING_CODE},
                SchemaConstants.WHERE_MEETING_ROWID, new String[] {Integer.toString(dbRowId)});
        cursor.moveToFirst();
        String[] details = new String[2];
        details[0] = cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_CODE));
        details[1] = cursor.getString(cursor.getColumnIndex(SchemaConstants.MEETING_DATE));
        return details;
    }

    private String createDownloadMessage(String[] date, String code) {
        return Resources.getInstance().getString(R.string.raceday_download_msg) + " "
               + code + " on " + (date[2] + "/" + date[1] + "/" + date[0])
               + Resources.getInstance().getString(R.string.download_msg_warn);
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
