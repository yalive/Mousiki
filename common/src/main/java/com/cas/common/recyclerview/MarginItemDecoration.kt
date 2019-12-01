package com.cas.common.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 **********************************
 * Created by Abdelhadi on 2019-05-10.
 **********************************
 */
class MarginItemDecoration(
    private val verticalMargin: Int = 0,
    private val horizontalMargin: Int = 0,
    private val firstItemAdditionalMargin: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val isVertical = layoutManager.orientation == RecyclerView.VERTICAL
        val first = parent.getChildAdapterPosition(view) == 0
        with(outRect) {
            if (isVertical) {
                val topMargin = if (first) verticalMargin + firstItemAdditionalMargin else verticalMargin
                top = topMargin
                left = horizontalMargin
                right = horizontalMargin
                bottom = verticalMargin
            } else {
                val leftMargin = if (first) horizontalMargin + firstItemAdditionalMargin else horizontalMargin
                left = leftMargin
                top = verticalMargin
                right = horizontalMargin
                bottom = verticalMargin
            }
        }
    }
}