package com.mcssoft.racemeetings2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.adapter.base.ParentViewHolder;
import com.mcssoft.racemeetings2.interfaces.IMeetingItemClickListener;

public class MeetingsViewHolder extends ParentViewHolder {

    public MeetingsViewHolder(View view, IMeetingItemClickListener listener) { //, IMeetingItemLongClickListener longListener) {
        super(view);
        tvMeetingCode = (TextView) view.findViewById(R.id.tv_id_meeting_code);
        tvVenueName = (TextView) view.findViewById(R.id.tv_id_venue_name);
        tvMeetingDate = (TextView) view.findViewById(R.id.tv_id_meeting_date);

        itemClickListener = listener;
        view.setOnClickListener(this);
//        itemLongClickListener = longListener;
//        view.setOnLongClickListener(this);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        if(itemClickListener != null){
            itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Accessors">
    public TextView getTvMeetingCode() { return tvMeetingCode; }
    public TextView getTvVenueName() { return tvVenueName; }
    public TextView getTvMeetingDate() { return tvMeetingDate; }
    //</editor-fold>

    private TextView tvMeetingCode;
    private TextView tvVenueName;
    private TextView tvMeetingDate;

    private IMeetingItemClickListener itemClickListener;
//    private IMeetingItemLongClickListener itemLongClickListener;
}
