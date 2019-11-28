package com.cas.musicplayer.ui.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.utils.dpToPixel


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeListSpacingItemDecoration(private val spacing: Int, private var edgeMargin: Int = 0) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position

        val adapter = parent.adapter as HomeAdapter

        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val gridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanSize = gridLayoutManager.spanSizeLookup.getSpanSize(position)

        val totalSpanSize = gridLayoutManager.spanCount
        val c = layoutParams.spanIndex / spanSize // column index
        val n = totalSpanSize / spanSize // num columns

        val leftPadding = spacing * ((n - c) / n) + edgeMargin
        val rightPadding = spacing * ((c + 1) / n) + edgeMargin

        val type = adapter.getItemViewType(position)

        if (type != HomeAdapter.TYPE_NEW_RELEASE && type != HomeAdapter.TYPE_CHART && type != HomeAdapter.TYPE_FEATURED) {
            outRect.left = leftPadding
            outRect.right = rightPadding
        }

        if (type != HomeAdapter.TYPE_NEW_RELEASE && type != HomeAdapter.TYPE_CHART && type != HomeAdapter.TYPE_HEADER) {
            if (position < totalSpanSize) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }

        if (type == HomeAdapter.TYPE_FEATURED) {
            outRect.top = spacing
        }

    }
}