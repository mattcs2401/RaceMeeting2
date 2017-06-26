package com.mcssoft.racemeetings2.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.adapter.base.ParentViewHolder;
import com.mcssoft.racemeetings2.interfaces.IItemClickListener;
import com.mcssoft.racemeetings2.interfaces.IItemExpandClickListener;

public class MeetingsViewHolder extends ParentViewHolder {

    public MeetingsViewHolder(View view, boolean expanded) {
        super(view);
        Log.d(getClass().getSimpleName(), "constructor");

        this.view = view;
        this.expanded = expanded;
        tvMeetingCode = (TextView) view.findViewById(R.id.tv_id_meeting_code);
        tvVenueName = (TextView) view.findViewById(R.id.tv_id_venue_name);
        tvMeetingDate = (TextView) view.findViewById(R.id.tv_id_meeting_date);

        tvWeatherDesc = (TextView) view.findViewById(R.id.id_tv_weather_val);
        tvTrackDesc = (TextView) view.findViewById(R.id.id_tv_track_val);

        ivExpand = (ImageView) view.findViewById(R.id.iv_meeting_expand_collapse);

        if(expanded) {
            ivExpand.setImageResource(R.drawable.ic_arrow_up);
        } else {
            ivExpand.setImageResource(R.drawable.ic_arrow_down);
        }
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
        Log.d(getClass().getSimpleName(), "onClick");
        int position = getAdapterPosition();
        if(view instanceof ImageView) {

            if(!expanded) {
                expanded = true;
                iecListener.onItemClick(position, true);
            } else {
                expanded = false;
                iecListener.onItemClick(position, false);
            }
        } else {
            icListener.onItemClick(view, position);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Accessors">
    public TextView getTvMeetingCode() { return tvMeetingCode; }
    public TextView getTvVenueName() { return tvVenueName; }
    public TextView getTvMeetingDate() { return tvMeetingDate; }
    public ImageView getIvExpand() { return ivExpand; }
    public TextView getTvWeatherDesc() { return tvWeatherDesc; }
    public TextView getTvTrackDesc() { return tvTrackDesc; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private TextView tvMeetingCode;
    private TextView tvVenueName;
    private TextView tvMeetingDate;
    private ImageView ivExpand;
    private TextView tvWeatherDesc;
    private TextView tvTrackDesc;
    private View view;
    private IItemClickListener icListener;
    private boolean expanded;
    private IItemExpandClickListener iecListener;
    //</editor-fold>
}
