package com.mcssoft.racemeetings2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.interfaces.IItemClickListener;
import com.mcssoft.racemeetings2.interfaces.IItemLongClickListener;

public class MeetingsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public MeetingsViewHolder(View view) {
        super(view);
//        tvEmptyView = (TextView) view.findViewById(R.id.id_tv_nothingToShow);
    }

    public MeetingsViewHolder(View view, IItemClickListener listener) { //, IItemLongClickListener longListener) {
        super(view);
        tvMeetingCode = (TextView) view.findViewById(R.id.tv_id_meeting_code);
        tvVenueName = (TextView) view.findViewById(R.id.tv_id_venue_name);

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
    //</editor-fold>

    private TextView tvMeetingCode;
    private TextView tvVenueName;

    private IItemClickListener itemClickListener;
//    private IItemLongClickListener itemLongClickListener;
}
