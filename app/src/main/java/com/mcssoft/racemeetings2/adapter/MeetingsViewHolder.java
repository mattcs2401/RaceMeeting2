package com.mcssoft.racemeetings2.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.adapter.base.ParentViewHolder;
import com.mcssoft.racemeetings2.interfaces.IItemClickListener;
import com.mcssoft.racemeetings2.interfaces.IItemExpandClickListener;

public class MeetingsViewHolder extends ParentViewHolder {

    public MeetingsViewHolder(View view) {
        super(view);
        this.view = view;
        tvMeetingCode = (TextView) view.findViewById(R.id.tv_id_meeting_code);
        tvVenueName = (TextView) view.findViewById(R.id.tv_id_venue_name);
        tvMeetingDate = (TextView) view.findViewById(R.id.tv_id_meeting_date);
        ivExpand = (ImageView) view.findViewById(R.id.iv_meeting_expand);
    }

    public void setItemClickListener(IItemClickListener listener) {
        icListener = listener;
        view.setOnClickListener(this);
    }

    public void setItemExpandClickListener(IItemExpandClickListener listener) {
        iecListener = listener;
        ivExpand.setOnClickListener(this);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        if(view instanceof ImageView) {
            iecListener.onItemClick(view, getAdapterPosition(), true);
        } else {
            icListener.onItemClick(view, getAdapterPosition());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Accessors">
    public TextView getTvMeetingCode() { return tvMeetingCode; }
    public TextView getTvVenueName() { return tvVenueName; }
    public TextView getTvMeetingDate() { return tvMeetingDate; }
    public ImageView getIvExpand() { return ivExpand; }
    //</editor-fold>

    private TextView tvMeetingCode;
    private TextView tvVenueName;
    private TextView tvMeetingDate;
    private ImageView ivExpand;

    private View view;
    private IItemClickListener icListener;
    private IItemExpandClickListener iecListener;
}
