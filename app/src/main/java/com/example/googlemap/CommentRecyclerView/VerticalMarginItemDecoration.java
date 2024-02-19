package com.example.googlemap.CommentRecyclerView;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VerticalMarginItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalMarginInPx;

    public VerticalMarginItemDecoration(int verticalMarginInPx) {
        this.verticalMarginInPx = verticalMarginInPx;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top= verticalMarginInPx;
        outRect.bottom=verticalMarginInPx;
    }
}
