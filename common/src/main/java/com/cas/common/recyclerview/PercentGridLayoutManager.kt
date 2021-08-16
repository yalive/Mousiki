package com.cas.common.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PercentGridLayoutManager(
    context: Context,
    spanCount: Int = 3,
    private val percent: Double = 0.9,
) : GridLayoutManager(context, spanCount, HORIZONTAL, false) {
    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = (width * percent).toInt()
        return true
    }
}