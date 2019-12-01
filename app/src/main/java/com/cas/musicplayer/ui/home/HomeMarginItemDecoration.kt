package com.cas.musicplayer.ui.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.utils.dpToPixel


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeMarginItemDecoration(
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val context = parent.context
        val position = parent.getChildAdapterPosition(view)
        val first = position == 0
        with(outRect) {
            if (first) {
                top = context.dpToPixel(16f)
            } else if (position == 1) {
                top = context.dpToPixel(16f)
            } else if (position == 3) {
                top = context.dpToPixel(16f)
            } else if (position == 5) {
                top = context.dpToPixel(16f)
            } else if (position == 6) {
                bottom = context.dpToPixel(48f)
            }
        }
    }
}