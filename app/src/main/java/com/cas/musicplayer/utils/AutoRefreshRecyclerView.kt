package com.cas.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 ************************************
 * Created by Abdelhadi on 11/30/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
class AutoRefreshRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mRequestedLayout = false

    @SuppressLint("WrongCall")
    override fun requestLayout() {
        super.requestLayout()
        if (!mRequestedLayout) {
            mRequestedLayout = true
            post {
                mRequestedLayout = false
                layout(left, top, right, bottom)
                onLayout(false, left, top, right, bottom)
            }
        }
    }
}