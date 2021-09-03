package com.cas.musicplayer.ui.local.videos.vplayer

import android.content.Context
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.cas.musicplayer.R
import java.lang.Exception

class DoubleTapPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CustomStyledPlayerView(context, attrs, defStyleAttr) {

    private val gestureDetector: GestureDetectorCompat
    private val gestureListener: DoubleTapGestureListener
    private var controller: PlayerDoubleTapListener? = null

    private var controllerRef: Int


    var isDoubleTapEnabled: Boolean

    private var doubleTapDelay: Long

    init {
        controllerRef = -1
        gestureListener = DoubleTapGestureListener(this)
        gestureDetector = GestureDetectorCompat(context, gestureListener)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DoubleTapPlayerView, 0, 0)
            controllerRef =
                a.getResourceId(R.styleable.DoubleTapPlayerView_dtpv_controller, -1) ?: -1
            a.recycle()
        }
        isDoubleTapEnabled = true
        doubleTapDelay = 700L
    }

    private fun getController(): PlayerDoubleTapListener? {
        return gestureListener.controls
    }

    private fun setController(value: PlayerDoubleTapListener) {
        gestureListener.controls = value
        controller = value
    }

    fun getDoubleTapDelay(): Long {
        return gestureListener.doubleTapDelay
    }

    fun setDoubleTapDelay(value: Long) {
        gestureListener.doubleTapDelay = value
        doubleTapDelay = value
    }

    fun controller(controller: PlayerDoubleTapListener): DoubleTapPlayerView {
        setController(controller)
        return this
    }

    val isInDoubleTapMode: Boolean
        get() = gestureListener.isDoubleTapping


    fun keepInDoubleTapMode() {
        gestureListener.keepInDoubleTapMode()
    }

    fun cancelInDoubleTapMode() {
        gestureListener.cancelInDoubleTapMode()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isDoubleTapEnabled) {
            val consumed = gestureDetector.onTouchEvent(ev)

            return if (!consumed) super.onTouchEvent(ev) else true
        }
        return super.onTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (controllerRef != -1) {
            try {
                val view = (parent as View).findViewById<View>(
                    controllerRef
                )
                if (view is PlayerDoubleTapListener) {
                    controller(view as PlayerDoubleTapListener)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private class DoubleTapGestureListener(private val rootView: CustomStyledPlayerView) :
        SimpleOnGestureListener() {
        private val mHandler: Handler = Handler()
        private val mRunnable: Runnable
        var controls: PlayerDoubleTapListener? = null
        var isDoubleTapping = false
        var doubleTapDelay: Long

        fun keepInDoubleTapMode() {
            isDoubleTapping = true
            mHandler.removeCallbacks(mRunnable)
            mHandler.postDelayed(mRunnable, doubleTapDelay)
        }

        fun cancelInDoubleTapMode() {
            mHandler.removeCallbacks(mRunnable)
            isDoubleTapping = false
            if (controls != null) controls!!.onDoubleTapFinished()
        }

        override fun onDown(e: MotionEvent): Boolean {
            // Used to override the other methods
            if (isDoubleTapping) {
                if (controls != null) controls!!.onDoubleTapProgressDown(e.x, e.y)
                return true
            }
            return super.onDown(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isDoubleTapping) {
                if (controls != null) controls!!.onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onSingleTapUp(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isDoubleTapping) return true
            //return rootView.performClick()
            return rootView.tap()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // First tap (ACTION_DOWN) of both taps
            if (!isDoubleTapping) {
                isDoubleTapping = true
                keepInDoubleTapMode()
                if (controls != null) controls!!.onDoubleTapStarted(e.x, e.y)
            }
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // Second tap (ACTION_UP) of both taps
            if (e.actionMasked == MotionEvent.ACTION_UP && isDoubleTapping) {
                if (controls != null) controls!!.onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onDoubleTapEvent(e)
        }

        companion object {
            private const val TAG = ".DTGListener"
            private const val DEBUG = false
        }

        init {
            mRunnable = Runnable {
                isDoubleTapping = false
                isDoubleTapping = false
                if (controls != null) controls!!.onDoubleTapFinished()
            }
            doubleTapDelay = 650L
        }
    }


}