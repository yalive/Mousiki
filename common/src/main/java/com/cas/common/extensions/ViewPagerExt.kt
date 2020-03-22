package com.cas.common.extensions

import androidx.viewpager.widget.ViewPager


inline fun ViewPager.doOnPageSelected(
    crossinline pageSelected: (Int) -> Unit
) {
    addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            pageSelected(position)
        }
    })
}