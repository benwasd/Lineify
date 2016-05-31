package ch.bfh.ti.lineify.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TouchListener implements RecyclerView.OnItemTouchListener {
    private final IItemTouchListener itemTouchListener;
    private final GestureDetector gestureDetector;

    public TouchListener(Context context, RecyclerView recyclerView, IItemTouchListener itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && TouchListener.this.itemTouchListener != null) {
                    TouchListener.this.itemTouchListener.onClick(child, recyclerView.getChildAdapterPosition(child), true);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && this.itemTouchListener != null && this.gestureDetector.onTouchEvent(e)) {
            this.itemTouchListener.onClick(child, rv.getChildAdapterPosition(child), false);
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
