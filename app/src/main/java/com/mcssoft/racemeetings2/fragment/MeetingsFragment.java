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
import com.mcssoft.racemeetings2.utility.ListingDivider;
import com.mcssoft.racemeetings2.utility.Resources;

public class MeetingsFragment extends Fragment
        implements IMeetingItemClickListener {

    public MeetingsFragment() {
        initialise();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setKeyAction();   // establish local variables for what we are going to show.

        if(isEmptyView) {
            rootView = inflater.inflate(R.layout.fragment_meetings_empty, container, false);
            showTitle(null);
        } else {
            rootView = inflater.inflate(R.layout.fragment_common, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!isEmptyView) {
            DatabaseOperations dbOper = new DatabaseOperations(getActivity());

            if(showDay) {
                if(dbOper.checkMeetingDate(date)) {
                    cursor = dbOper.getSelectionFromTable(SchemaConstants.MEETINGS_TABLE, null,
                            SchemaConstants.WHERE_MEETING_DATE, new String[] {date});

                    showTitle("Meetings for " + date);
                }
            } else if(showAll) {
                if(dbOper.checkTableRowCount(SchemaConstants.MEETINGS_TABLE)) {
                    cursor = dbOper.getAllFromTable(SchemaConstants.MEETINGS_TABLE);

                    showTitle("All Meetings");
                }
            }

            setMeetingAdapter();
            setRecyclerView(rootView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        cursor = null;
    }

    @Override
    public void onItemClick(View view, int position) {
//        this.position = position;
        int dbRowId = getDbRowId(position);
        Intent intent = new Intent(getActivity(), RacesActivity.class);
        intent.putExtra(Resources.getInstance().getString(R.string.meetings_db_rowid_key),  dbRowId);
        startActivity(intent);
//        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
//        popupMenu.inflate(R.menu.meetings_context_menu);
//        popupMenu.setOnMenuItemClickListener(this);
//        popupMenu.show();
    }

    private void setRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.id_rv_listing);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new ListingDivider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingsAdapter);
    }

    private void setMeetingAdapter() {
        meetingsAdapter = new MeetingsAdapter();
        if(isEmptyView) {
            meetingsAdapter.setEmptyView(true);
        } else {
            if(cursor != null) {
                meetingsAdapter.swapCursor(cursor);
                meetingsAdapter.setOnItemClickListener(this);

                if (cursor.getCount() == 0) {
                    meetingsAdapter.setEmptyView(true);
                } else {
                    meetingsAdapter.setEmptyView(false);
                }
            } else {
                meetingsAdapter.setEmptyView(true);
            }
        }
    }

    private int getDbRowId(int position) {
        meetingsAdapter.getItemId(position);
        Cursor cursor = meetingsAdapter.getCursor();
        return cursor.getInt(cursor.getColumnIndex(SchemaConstants.MEETING_ROWID));
    }

    /**
     * Get the key from the arguments and set variables as to what "action" to perform.
     */
    private void setKeyAction() {
        Bundle args = getArguments();
        String key = (String) args.keySet().toArray()[0];  // should only be one key at a time.
        switch(key) {
            case "meetings_show_day_key":
                showDay = true;
                date = (String) args.get(key);
                break;
            case "meetings_show_all_key":
                showAll = true;
                break;
            case "meetings_show_empty_key":
                isEmptyView = true;
                break;
            default: isEmptyView = true;
        }
    }

    private void initialise() {
        date = null;
        cursor = null;
        rootView = null;
        meetingsAdapter = null;
        showAll = showDay = isEmptyView = false;
    }

    private void showTitle(String title) {
//        MeetingsActivity activity = ((MeetingsActivity) getActivity());
        TextView textView =  ((TextView)(((MeetingsActivity) getActivity()).getToolbar())
                .findViewById(R.id.id_tv_toolbar));
        if(title != null) {
            textView.setText(title);
        } else {
            textView.setText("");
        }
    }

    private String date;          // show Meetings for this date (may not be used).
    private View rootView;
    private Cursor cursor;        // the current result set from the database to populate adapter.

    private boolean showDay;
    private boolean showAll;      // flag, show all Meetings.
    private boolean isEmptyView;  // flag, nothing to show.

    private MeetingsAdapter meetingsAdapter;
}
