package ch.bfh.ti.lineify.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable divider;
    private int orientation;

    public DividerItemDecoration(Context context, int orientation) {
        final int[] ATTRS = new int[]{ android.R.attr.listDivider };
        final TypedArray typedArray = context.obtainStyledAttributes(ATTRS);

        this.divider = typedArray.getDrawable(0);
        typedArray.recycle();
        this.setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }

        this.orientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (this.orientation == VERTICAL_LIST) {
            this.drawVertical(canvas, parent);
        }
        else {
            this.drawHorizontal(canvas, parent);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (this.orientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, this.divider.getIntrinsicHeight());
        }
        else {
            outRect.set(0, 0, this.divider.getIntrinsicWidth(), 0);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + this.divider.getIntrinsicHeight();
            this.divider.setBounds(left, top, right, bottom);
            this.divider.draw(canvas);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + this.divider.getIntrinsicHeight();
            this.divider.setBounds(left, top, right, bottom);
            this.divider.draw(canvas);
        }
    }
}