package ch.bfh.ti.lineify.ui.adapter;

import android.view.View;

public interface IItemTouchListener {
    void onClick(View view, int position, boolean isLongClick);
}