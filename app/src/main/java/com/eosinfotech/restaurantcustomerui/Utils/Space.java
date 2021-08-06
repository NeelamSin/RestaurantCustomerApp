package com.eosinfotech.restaurantcustomerui.Utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


//Recyclerview decoration class to add space between items
public class Space extends RecyclerView.ItemDecoration {
    int space;

    public Space(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //Check if its header or footer because we don't want to add space for header and footer
        if (!(parent.getChildLayoutPosition(view) == 0 | parent.getChildLayoutPosition(view) == parent.getAdapter().getItemCount() - 1)) {
            if (parent.getChildLayoutPosition(view) == 1) {
                outRect.top = space;
            }
            outRect.right = space;
            outRect.left = space;
            outRect.bottom = space;
        }

    }
}
