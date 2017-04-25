package com.mcssoft.racemeetings2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

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
}
