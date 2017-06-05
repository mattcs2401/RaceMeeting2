package com.mcssoft.racemeetings2.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.activity.MeetingsActivity;
import com.mcssoft.racemeetings2.activity.RacesActivity;
import com.mcssoft.racemeetings2.adapter.MeetingsAdapter;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IMeetingItemClickListener;
import com.mcssoft.racemeetings2.utility.Preferences;
import com.mcssoft.racemeetings2.utility.Resources;

public class MeetingsFragment extends Fragment
        implements IMeetingItemClickListener {

    public MeetingsFragment() {
        initialise();
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setKeyAction();   // establish local variables for what we are going to show.

        if(isEmptyView) {
            rootView = inflater.inflate(R.layout.fragment_meetings_empty, container, false);
            showToolbarTitle(null);
        } else {
            rootView = inflater.inflate(R.layout.fragment_meetings, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!isEmptyView) {
            dbOper = new DatabaseOperations(getActivity());

            if(showDay) {
                getMeetingsOnDate();

            } else if(showAll) {
                getAllMeetings();
            }

            if(cursor == null) {
                // No rows.
                isEmptyView = true;
            }
            setMeetingAdapter();
            setRecyclerView(rootView);

        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onItemClick(View view, int position) {
        int dbRowId = getDbRowId(position);
        Intent intent = new Intent(getActivity(), RacesActivity.class);
        intent.putExtra(Resources.getInstance().getString(R.string.meetings_db_rowid_key),  dbRowId);
        startActivity(intent);
        /*PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.meetings_context_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void setRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.id_rv_meetings_listing);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingsAdapter);
    }

    private void setMeetingAdapter() {
        meetingsAdapter = new MeetingsAdapter();
        if(!isEmptyView && cursor != null && cursor.getCount() > 0) {
            meetingsAdapter.setEmptyView(false);
            meetingsAdapter.swapCursor(cursor);
            meetingsAdapter.setOnItemClickListener(this);
            if(showAll) {
                meetingsAdapter.setShowDate(true);
            }
        } else {
            meetingsAdapter.setEmptyView(true);
        }
    }

    private int getDbRowId(int position) {
        meetingsAdapter.getItemId(position);
        Cursor cursor = meetingsAdapter.getCursor();
        int dbRowId = cursor.getInt(cursor.getColumnIndex(SchemaConstants.MEETING_ROWID));
        return dbRowId;
    }

    /**
     * Get the key from the arguments and set variables as to what "action" to perform.
     */
    private void setKeyAction() {
        String key = null;
        Bundle args = getArguments();
        Object[] keySet = args.keySet().toArray();
        key = (String) keySet[0];
        if(keySet.length > 1) {
            code = args.getString((String) keySet[1]);
            hasCode = true;
        }

        if(key.equals(Resources.getInstance().getString(R.string.meetings_show_day_key))) {
            showDay = true;
            date = (String) args.get(key);
        } else if(key.equals(Resources.getInstance().getString(R.string.meetings_show_all_key))) {
            showAll = true;
        } else if(key.equals(Resources.getInstance().getString(R.string.meetings_show_empty_key))) {
            isEmptyView = true;
        } else {
            isEmptyView = true;
        }
    }

    private void initialise() {
        date = code = null;
        cursor = null;
        rootView = null;
        meetingsAdapter = null;
        showAll = showDay = isEmptyView = hasCode = false;
    }

    private void showToolbarTitle(String title) {
        TextView textView =  ((TextView)(((MeetingsActivity) getActivity()).getToolbar())
                .findViewById(R.id.id_tv_toolbar));
        if(title != null) {
            textView.setText(title);
        } else {
            textView.setText("");
        }
    }

    private void getMeetingsOnDate() {
        if(hasCode) {
            // get meetings by date and code
            if(dbOper.checkMeetingDate(date, "%" + code)) {
                cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE, null,
                        SchemaConstants.WHERE_MEETING_DATE_CODE, new String[] {date, "%" + code});

                showToolbarTitle("(" + code + ")" + " Meetings " + date);
            }
        } else {
            // get meetings by date.
            if(dbOper.checkMeetingDate(date, null)) {
                cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE, null,
                            SchemaConstants.WHERE_MEETING_DATE, new String[] {date});

                showToolbarTitle("Meetings " + date);
            }
        }
    }

    private void getAllMeetings() {
        if(hasCode) {
            // get all meetings by code
            if(dbOper.checkTableRowCount(SchemaConstants.MEETINGS_TABLE, SchemaConstants.WHERE_MEETING_CODE, new String[] {"%" + code})) {
                cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE, null,
                        SchemaConstants.WHERE_MEETING_CODE, new String[] {"%" + code});

                showToolbarTitle("(" + code + ")" + " All Meetings");
            }
        } else {
            // get all meetings.
            if(dbOper.checkTableRowCount(SchemaConstants.MEETINGS_TABLE, null, null)) {
                cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE, null, null, null);

                showToolbarTitle("All Meetings");
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private String code;          // meeting race code (may not exist);
    private String date;          // show Meetings for this date (may not be used).
    private View rootView;
    private Cursor cursor;        // the current result set from the database to populate adapter.
    private boolean hasCode;      // flag, race code in arguments.
    private boolean showDay;      // flag, show meetings on date.
    private boolean showAll;      // flag, show all Meetings.
    private boolean isEmptyView;  // flag, nothing to show.

    private DatabaseOperations dbOper;
    private MeetingsAdapter meetingsAdapter;
    //</editor-fold>
}
