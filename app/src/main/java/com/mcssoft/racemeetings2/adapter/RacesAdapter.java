package com.mcssoft.racemeetings2.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.interfaces.IMeetingItemClickListener;
import com.mcssoft.racemeetings2.interfaces.IRaceItemClickListener;

public class RacesAdapter extends RecyclerView.Adapter<RacesViewHolder> {

    @Override
    public RacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RacesViewHolder holder, int position) {
        adapaterOnBindViewHolder(holder, position);;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void adapaterOnBindViewHolder(RacesViewHolder holder, int position) {
//        cursor.moveToPosition(position);
//        holder.getTvMeetingCode().setText(cursor.getString(meetingCodeNdx));
//        holder.getTvVenueName().setText(cursor.getString(meetingVenueNdx));
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

//        idColNdx = cursor.getColumnIndex(SchemaConstants.MEETING_ROWID);
//        meetingCodeNdx = cursor.getColumnIndex(SchemaConstants.MEETING_CODE);
//        meetingVenueNdx = cursor.getColumnIndex(SchemaConstants.MEETING_VENUE);

            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() { return cursor; }

    public void setOnItemClickListener(IRaceItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setEmptyView(boolean emptyView) {
        this.emptyView = emptyView;
    }

    private boolean emptyView;
    private int idColNdx;
    private Cursor cursor;
    private IRaceItemClickListener itemClickListener;
}
