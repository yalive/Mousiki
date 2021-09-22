package com.cas.musicplayer.ui.home.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class FlingRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling(velocityX, (velocityY * FLING_SPEED_FACTOR).toInt());
    }

    companion object {
        private const val FLING_SPEED_FACTOR = 1.3f
    }
}