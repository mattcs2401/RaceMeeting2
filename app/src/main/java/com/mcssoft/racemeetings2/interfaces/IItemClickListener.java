package com.mcssoft.racemeetings2.interfaces;

import android.view.View;

/**
 * Interface implemented by the display Fragment, to the Adapter and then ViewHolder to respond to a
 * select on an item in the recyclerview.
 */
public interface IItemClickListener {
    /**
     * @param view The selected Adapter item view.
     * @param position Row position of the Adapter's item.
     */
    void onItemClick(View view, int position);
}
