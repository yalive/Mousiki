package com.cas.musicplayer.ui.home.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import com.crashlytics.android.Crashlytics
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 ***************************************
 * Created by Fayssel on 2019-12-17.
 ***************************************
 */
class InsetSlidingPanelView : SlidingUpPanelLayout {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val childCount = childCount
        for (index in 0 until childCount)
            getChildAt(index).dispatchApplyWindowInsets(insets)
        return insets
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        // workaround
        return try {
            super.drawChild(canvas, child, drawingTime)
        } catch (e: Exception) {
            false
        } catch (e: OutOfMemoryError) {
            false
        }
    }

    override fun draw(c: Canvas?) {
        try {
            super.draw(c)
        } catch (e: OutOfMemoryError) {
            Crashlytics.logException(Exception("Catch SlidingUpPanelLayout out of memory issue", e))
        }
    }

}
