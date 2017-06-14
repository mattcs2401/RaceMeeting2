package com.mcssoft.racemeetings2.interfaces;

import android.view.View;

/**
 * Interface from the ViewHolder back to the Adapter to respond to a select on an item in the
 * recyclerview. Indicates that the recyclerview item should be expanded or collapsed.
 */
public interface IItemExpandClickListener {
    /**
     * @param view The selected Adapter item view.
     * @param position Row position of the Adapter's item.
     * @param expanded True==expand, else false==collapse.
     */
    void onItemClick(View view, int position, boolean expanded);
}
