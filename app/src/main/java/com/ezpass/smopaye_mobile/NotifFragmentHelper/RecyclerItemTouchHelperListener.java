package com.ezpass.smopaye_mobile.NotifFragmentHelper;


import androidx.recyclerview.widget.RecyclerView;

/**
 * interface qui permet d'effectuer un glisser déposé
 *
 * @see RecyclerItemTouchHelperListener
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
