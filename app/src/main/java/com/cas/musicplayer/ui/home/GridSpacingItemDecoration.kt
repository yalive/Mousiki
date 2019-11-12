package com.cas.musicplayer.ui.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GridSpacingItemDecoration(val spanCount: Int, val spacing: Int, val includeEdge: Boolean) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing
            outRect.right = spacing
            outRect.top = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right =
                spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}