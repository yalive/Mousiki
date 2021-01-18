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
    verticalMargin: Int = 0,
    horizontalMargin: Int = 0,
    private val firstItemAdditionalMargin: Int = 0,
    private val leftMarginProvider: (Int) -> Int = { horizontalMargin },
    private val topMarginProvider: (Int) -> Int = { verticalMargin },
    private val rightMarginProvider: (Int) -> Int = { horizontalMargin },
    private val bottomMarginProvider: (Int) -> Int = { verticalMargin }
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val isVertical = layoutManager.orientation == RecyclerView.VERTICAL
        val position = parent.getChildAdapterPosition(view)
        val first = position == 0
        with(outRect) {
            if (isVertical) {
                val topMargin =
                    if (first) topMarginProvider(position) + firstItemAdditionalMargin else topMarginProvider(
                        position
                    )
                top = topMargin
                left = leftMarginProvider(position)
                right = rightMarginProvider(position)
                bottom = bottomMarginProvider(position)
            } else {
                val leftMargin =
                    if (first) leftMarginProvider(position) + firstItemAdditionalMargin else leftMarginProvider(
                        position
                    )
                left = leftMargin
                top = topMarginProvider(position)
                right = rightMarginProvider(position)
                bottom = bottomMarginProvider(position)
            }
        }
    }
}

class FirstItemMarginDecoration(
    private val verticalMargin: Int = 0,
    private val horizontalMargin: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val isVertical = layoutManager.orientation == RecyclerView.VERTICAL
        val first = parent.getChildAdapterPosition(view) == 0
        with(outRect) {
            if (first) {
                if (isVertical) {
                    top = verticalMargin
                } else {
                    left = horizontalMargin // RTL?
                }
            }
        }
    }
}