package com.cas.musicplayer.utils

import androidx.viewpager2.widget.ViewPager2

/**
 ************************************
 * Created by Abdelhadi on 11/28/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
abstract class OnUserPageChangeCallback : ViewPager2.OnPageChangeCallback() {

    private val scrollEvents = mutableListOf<Int>()

    override fun onPageSelected(position: Int) {
        val swipeByUser =
            !(scrollEvents.isEmpty() || !scrollEvents.contains(ViewPager2.SCROLL_STATE_DRAGGING))
        onPageSelected(position, swipeByUser)
    }

    override fun onPageScrollStateChanged(state: Int) {
        scrollEvents.add(state)
        if (state == ViewPager2.SCROLL_STATE_IDLE) {
            scrollEvents.clear()
        }
    }

    abstract fun onPageSelected(position: Int, fromUser: Boolean)
}

inline fun ViewPager2.doOnPageSelected(crossinline block: (Int, Boolean) -> Unit) {
    registerOnPageChangeCallback(object : OnUserPageChangeCallback() {
        override fun onPageSelected(position: Int, fromUser: Boolean) {
            block(position, fromUser)
        }
    })
}