package com.mcssoft.racemeetings2.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.adapter.MeetingsAdapter;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IItemClickListener;
import com.mcssoft.racemeetings2.utility.ListingDivider;

public class MeetingsFragment extends Fragment
        implements IItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.meetings_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DatabaseOperations dbOper = new DatabaseOperations(getActivity());
        cursor = dbOper.getAllFromTable(SchemaConstants.MEETINGS_TABLE);

        setMeetingAdapter();
        setRecyclerView(rootView);
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
        // TODO - show races for meeting.
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
        meetingsAdapter.setOnItemClickListener(this);
//        meetingsAdapter.setOnItemLongClickListener(this);
        meetingsAdapter.swapCursor(cursor);
        if(cursor.getCount() == 0) {
            meetingsAdapter.setEmptyView(true);
        } else {
            meetingsAdapter.setEmptyView(false);
        }
    }

    private int position;
    private View rootView;
    private Cursor cursor;
    private MeetingsAdapter meetingsAdapter;
}
