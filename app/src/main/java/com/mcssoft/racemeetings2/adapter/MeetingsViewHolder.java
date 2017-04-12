package com.mcssoft.racemeetings2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
//        tvRaceId = (TextView) view.findViewById(R.id.id_tv_)
//        tvRaceNo = (TextView) view.findViewById(R.id.id_tv_race_no);
//        tvRaceName = (TextView) view.findViewById(R.id.id_tv_race_name);
//        tvRaceTime = (TextView) view.findViewById(R.id.id_tv_race_time);
//        tvRaceClass = (TextView) view.findViewById(R.id.id_tv_race_class);
//        tvRaceDistance = (TextView) view.findViewById(R.id.id_tv_race_distance);
//        tvRaceRating = (TextView) view.findViewById(R.id.id_tv_race_rating);
//        tvRacePrize = (TextView) view.findViewById(R.id.id_tv_race_prize);

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

    private IItemClickListener itemClickListener;
//    private IItemLongClickListener itemLongClickListener;
}
