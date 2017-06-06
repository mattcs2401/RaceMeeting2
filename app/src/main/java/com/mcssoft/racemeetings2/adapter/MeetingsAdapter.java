package com.mcssoft.racemeetings2.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IMeetingItemClickListener;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsViewHolder> {

    public MeetingsAdapter() {
        isEmptyView = false;
        showDate = false;
        cursor = null;
    }

    @Override
    public MeetingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);
            return new MeetingsViewHolder(view, itemClickListener); //, itemLongClickListener);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MeetingsViewHolder holder, int position) {
        adapaterOnBindViewHolder(holder, position);
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
        if(!isEmptyView && (newCursor != null) && (newCursor.getCount() > 0)) {
            cursor = newCursor;
            cursor.moveToFirst();

            idColNdx = cursor.getColumnIndex(SchemaConstants.MEETING_ROWID);
            meetingCodeNdx = cursor.getColumnIndex(SchemaConstants.MEETING_CODE);
            meetingVenueNdx = cursor.getColumnIndex(SchemaConstants.MEETING_VENUE);
            meetingDateNdx = cursor.getColumnIndex(SchemaConstants.MEETING_DATE);

            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(IMeetingItemClickListener listener) {
        this.itemClickListener = listener;
    }

//    public void setOnItemLongClickListener(IMeetingItemLongClickListener listener) {
//        this.itemLongClickListener = listener;
//    }

    public Cursor getCursor() { return cursor; }

    public void setEmptyView(boolean isEmptyView) {
        this.isEmptyView = isEmptyView;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    private void adapaterOnBindViewHolder(MeetingsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.getTvMeetingCode().setText(cursor.getString(meetingCodeNdx));
        holder.getTvVenueName().setText(cursor.getString(meetingVenueNdx));

        if(showDate) {
            holder.getTvMeetingDate().setText(cursor.getString(meetingDateNdx));
        }
    }


    private View view;
    private Cursor cursor;
    private boolean isEmptyView;
    private boolean showDate;
    private int idColNdx;
    private int meetingCodeNdx;
    private int meetingVenueNdx;
    private int meetingDateNdx;
    private IMeetingItemClickListener itemClickListener;
//    private IMeetingItemLongClickListener itemLongClickListener;
}
