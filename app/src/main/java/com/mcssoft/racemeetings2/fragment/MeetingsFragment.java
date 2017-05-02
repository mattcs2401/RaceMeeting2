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

public class MeetingsFragment extends Fragment
        implements IMeetingItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args != null) {
            isEmptyView = args.getBoolean("meetings_empty_view_key");
            if(isEmptyView) {
                rootView = inflater.inflate(R.layout.meetings_fragment_empty, container, false);
            }
        } else {
            rootView = inflater.inflate(R.layout.meetings_fragment, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DatabaseOperations dbOper = new DatabaseOperations(getActivity());
        cursor = dbOper.getAllFromTable(SchemaConstants.MEETINGS_TABLE);

        setMeetingAdapter();

        if(!isEmptyView) {
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
        this.position = position;
        int dbRowId = getDbRowId(position);
        Intent intent = new Intent(getActivity(), RacesActivity.class);
        intent.putExtra(Resources.getInstance().getString(R.string.meetings_db_rowid_key),  dbRowId); // getDbRowId(position));
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

    private int position;
    private boolean isEmptyView;
    private View rootView;
    private Cursor cursor;
    private MeetingsAdapter meetingsAdapter;
}
