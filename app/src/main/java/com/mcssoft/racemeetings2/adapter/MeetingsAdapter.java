package com.mcssoft.racemeetings2.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IItemClickListener;
import com.mcssoft.racemeetings2.interfaces.IItemExpandClickListener;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsViewHolder>
        implements IItemExpandClickListener {

    public MeetingsAdapter() {
        doExpand = false;
        isEmptyView = false;
        showDate = false;
        cursor = null;
    }

    @Override
    public MeetingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            MeetingsViewHolder mvh;
            if(doExpand) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row_expanded, parent, false);
                mvh = new MeetingsViewHolder(view, true);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);
                mvh = new MeetingsViewHolder(view, false);
            }
            mvh.setItemClickListener(icListener);
            mvh.setItemExpandClickListener(this);
            return mvh;
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MeetingsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.getTvMeetingCode().setText(cursor.getString(meetingCodeNdx));
        holder.getTvVenueName().setText(cursor.getString(meetingVenueNdx));

        if(showDate) {
            holder.getTvMeetingDate().setText(cursor.getString(meetingDateNdx));
        }

        if(doExpand) {
            String weatherDesc = cursor.getString(meetingWeatherDescNdx);
            if(weatherDesc == null) {
                weatherDesc = "NA";
            }
            holder.getTvWeatherDesc().setText(weatherDesc);

            String trackDesc = cursor.getString(meetingTrackDescNdx);
            if(trackDesc == null) {
                trackDesc = "NA";
            } else {
                trackDesc = trackDesc + " " + cursor.getString(meetingTrackRatingNdx);
            }
            holder.getTvTrackDesc().setText(trackDesc);
        }
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

            meetingWeatherDescNdx = cursor.getColumnIndex(SchemaConstants.MEETING_WEATHER_DESC);
            meetingTrackDescNdx = cursor.getColumnIndex(SchemaConstants.MEETING_TRACK_DESC);
            meetingTrackRatingNdx = cursor.getColumnIndex(SchemaConstants.MEETING_TRACK_RATING);

            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(IItemClickListener listener) {
        this.icListener = listener;
    }

    /**
     * Respond to click evcents on the expand icon.
     * @param position Row position of the Adapter's item.
     * @param expand True - expand, else false - collapse.
     */
    @Override
    public void onItemClick(int position, boolean expand) {
        if(expand) {
            doExpand = true;
        } else {
            doExpand = false;
        }
        notifyItemChanged(position);
    }

    public Cursor getCursor() { return cursor; }

    public void setEmptyView(boolean isEmptyView) {
        this.isEmptyView = isEmptyView;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    private View view;
    private Cursor cursor;
    private boolean doExpand;
    private boolean isEmptyView;
    private boolean showDate;
    private int idColNdx;
    private int meetingCodeNdx;
    private int meetingVenueNdx;
    private int meetingDateNdx;
    private int meetingWeatherDescNdx;
    private int meetingTrackDescNdx;
    private int meetingTrackRatingNdx;
    private IItemClickListener icListener;
}
