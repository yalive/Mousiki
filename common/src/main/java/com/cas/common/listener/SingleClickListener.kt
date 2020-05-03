package com.cas.common.listener

import android.view.View

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
class SingleClickListener(val listener: (View) -> Unit) : View.OnClickListener {

    override fun onClick(view: View) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
            previousClickTimeMillis = currentTimeMillis
            listener.invoke(view)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 200L
        private var previousClickTimeMillis = 0L
    }
}