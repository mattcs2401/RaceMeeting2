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

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.activity.RacesActivity;
import com.mcssoft.racemeetings2.adapter.MeetingsAdapter;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IMeetingItemClickListener;
import com.mcssoft.racemeetings2.utility.ListingDivider;
import com.mcssoft.racemeetings2.utility.Resources;

import java.util.Set;

public class MeetingsFragment extends Fragment
        implements IMeetingItemClickListener {

    public MeetingsFragment() {
        initialise();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setKeyAction();   // establish local variables for what we are going to show.

        if(isEmptyView) {
            rootView = inflater.inflate(R.layout.meetings_fragment_empty, container, false);
        } else {
            rootView = inflater.inflate(R.layout.meetings_fragment, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!isEmptyView) {
            DatabaseOperations dbOper = new DatabaseOperations(getActivity());

            if(showAll) {
                cursor = dbOper.getAllFromTable(SchemaConstants.MEETINGS_TABLE);
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
    public void onDestroy() {
        super.onDestroy();
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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.id_rv_meetings_summary_listing);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ListingDivider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingsAdapter);
    }

    private void setMeetingAdapter() {
        meetingsAdapter = new MeetingsAdapter();
        if(isEmptyView) {
            meetingsAdapter.setEmptyView(true);
        } else {
            meetingsAdapter.setOnItemClickListener(this);
//        meetingsAdapter.setOnItemLongClickListener(this);
            meetingsAdapter.swapCursor(cursor);
            if (cursor.getCount() == 0) {
                meetingsAdapter.setEmptyView(true);
            } else {
                meetingsAdapter.setEmptyView(false);
            }
        }
    }

    private int getDbRowId(int position) {
        meetingsAdapter.getItemId(position);
        Cursor cursor = meetingsAdapter.getCursor();
        return cursor.getInt(cursor.getColumnIndex(SchemaConstants.MEETING_ROWID));
    }

    private void doKeyAction() {

    }

    private void setKeyAction() {
        Bundle args = getArguments();
        this.args = args;
        Set<String> keySet = args.keySet();
        for(String key : keySet) {
            // should only be one key., thought this was simpler than using if/else if etc.
            switch (key) {
                case "meetings_show_today_key":
                    // Meetings for today exist in the database.
                    showToday = true;
                    date = args.getString(key);
                    break;
//                case "meetings_show_today_download_key":
//                    // Meetings for today will need to be downloaded.
//                    downloadToday = true;
//                    date = args.getString(key);
//                    break;
                case "meetings_show_all_key":
                    // show all Meetings regardless.
                    showAll = true;
                    break;
                case "meetings_show_empty_key":
                    isEmptyView = true;
                    break;
            }
        }
    }

    private void initialise() {
        date = "";
        cursor = null;
        rootView = null;
        meetingsAdapter = null;
        showAll = showToday = downloadToday = isEmptyView = false;
    }

    private Bundle args;
    private String date;
//    private int position;
    private View rootView;
    private Cursor cursor;
    private boolean showToday;
    private boolean showAll;
    private boolean downloadToday;
    private boolean isEmptyView;
    private MeetingsAdapter meetingsAdapter;
}
