package com.cas.common.recyclerview

import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView


// https://takusemba.medium.com/customize-your-recyclerview-with-snaphelper-3f883b889f0d
class AlignLeftPagerSnapHelper : PagerSnapHelper() {

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        out[0] = if (layoutManager.canScrollHorizontally()) {
            getDistance(targetView, OrientationHelper.createHorizontalHelper(layoutManager))
        } else 0

        out[1] = if (layoutManager.canScrollVertically()) {
            getDistance(targetView, OrientationHelper.createVerticalHelper(layoutManager))
        } else 0
        return out
    }

    private fun getDistance(
        targetView: View?,
        helper: OrientationHelper
    ): Int {
        val childStart = helper.getDecoratedStart(targetView)
        val containerStart = helper.startAfterPadding
        return childStart - containerStart
    }
}