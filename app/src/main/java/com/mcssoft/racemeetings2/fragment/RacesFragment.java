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
import com.mcssoft.racemeetings2.adapter.RacesAdapter;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IRaceItemClickListener;
import com.mcssoft.racemeetings2.utility.ListingDivider;

public class RacesFragment extends Fragment
        implements IRaceItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.races_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        DatabaseOperations dbOper = new DatabaseOperations(getActivity());
//        cursor = dbOper.getAllFromTable(SchemaConstants.MEETINGS_TABLE);

        setRaceAdapter();
        setRecyclerView(rootView);
    }

    @Override
    public void onItemClick(View view, int position) {
        this.position = position;
        int dbRowId = getDbRowId(position);
//        Intent intent = new Intent(getActivity(), RacesActivity.class);
//        intent.putExtra(Resources.getInstance().getString(R.string.meetings_db_rowid_key),  dbRowId); // getDbRowId(position));
//        startActivity(intent);
    }

    private void setRaceAdapter() {
        racesAdapter = new RacesAdapter();
        racesAdapter.setOnItemClickListener(this);
//        meetingsAdapter.setOnItemLongClickListener(this);
        racesAdapter.swapCursor(cursor);
//        if(cursor.getCount() == 0) {
//            racesAdapter.setEmptyView(true);
//        } else {
//            racesAdapter.setEmptyView(false);
//        }
    }

    private void setRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.id_rv_races_summary_listing);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ListingDivider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(racesAdapter);
    }

    private int getDbRowId(int position) {
        racesAdapter.getItemId(position);
        Cursor cursor = racesAdapter.getCursor();
        return cursor.getInt(cursor.getColumnIndex(SchemaConstants.RACE_ROWID));
    }

    private Cursor cursor;
    private int position;
    private View rootView;
    private RacesAdapter racesAdapter;

}
