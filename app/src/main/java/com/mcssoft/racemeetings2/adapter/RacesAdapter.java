package com.mcssoft.racemeetings2.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IRaceItemClickListener;

public class RacesAdapter extends RecyclerView.Adapter<RacesViewHolder> {

    @Override
    public RacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.race_row, parent, false);
            return new RacesViewHolder(view, itemClickListener); //, itemLongClickListener);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(RacesViewHolder holder, int position) {
        adapaterOnBindViewHolder(holder, position);;
    }

    @Override
    public int getItemCount() {
        if(isEmptyView) {
            return  1; // need to do this so the onCreateViewHolder fires.
        } else {
            if(cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(idColNdx);
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        if(cursor != null) {
            cursor.moveToFirst();
            idColNdx = cursor.getColumnIndex(SchemaConstants.RACE_ROWID);
            raceNoColNdx = cursor.getColumnIndex(SchemaConstants.RACE_NO);
            raceNameColNdx = cursor.getColumnIndex(SchemaConstants.RACE_NAME);
            raceTimeColNdx = cursor.getColumnIndex(SchemaConstants.RACE_TIME);
            raceDistColNdx = cursor.getColumnIndex(SchemaConstants.RACE_DIST);
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() { return cursor; }

    public void setOnItemClickListener(IRaceItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setEmptyView(boolean emptyView) {
        this.isEmptyView = emptyView;
    }

    private void adapaterOnBindViewHolder(RacesViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.getTvRaceNo().setText(cursor.getString(raceNoColNdx));
        holder.getTvRaceName().setText(cursor.getString(raceNameColNdx));
        holder.getTvRaceTime().setText(cursor.getString(raceTimeColNdx));
        holder.getTvRaceDist().setText(cursor.getString(raceDistColNdx));
    }

    private int idColNdx;
    private int raceNoColNdx;
    private int raceNameColNdx;
    private int raceTimeColNdx;
    private int raceDistColNdx;

    private View view;
    private Cursor cursor;
    private boolean isEmptyView;

    private IRaceItemClickListener itemClickListener;
}
