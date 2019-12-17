package com.cas.musicplayer.ui.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 ***************************************
 * Created by Fayssel on 2019-12-17.
 ***************************************
 */
class InsetSlidingPanelView : SlidingUpPanelLayout {

    constructor(context: Context) : super(context) {
        print("")
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        print("")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        print("")
    }

    override fun setOnApplyWindowInsetsListener(listener: OnApplyWindowInsetsListener?) {
        super.setOnApplyWindowInsetsListener(listener)
    }
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val childCount = childCount
        for (index in 0 until childCount)
            getChildAt(index).dispatchApplyWindowInsets(insets)
        return insets
    }
}