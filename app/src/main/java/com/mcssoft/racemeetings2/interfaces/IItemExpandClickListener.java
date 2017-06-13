package com.mcssoft.racemeetings2.interfaces;

import android.view.View;

public interface IItemExpandClickListener {
    /**
     * Interface from the ViewHolder back to the Adapter.
     * @param view TBA
     * @param position Row position of the Adapter's item.
     * @param expanded True==expand, else false==collapse.
     */
    void onItemClick(View view, int position, boolean expanded);
}
