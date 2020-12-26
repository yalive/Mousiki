package com.cas.musicplayer.ui.player

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlin.math.abs

const val TAG_HORZ = "HorizontalMotion"

class HorizontalMotion @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {


    private var mIsScrolling = false
    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var lastY = 0f
    private var lastX = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            lastY = ev.y
            lastX = ev.x
        }

        val yDiffLog: Int = abs(ev.y - lastY).toInt()
        val xDiffLog: Int = abs(ev.x - lastX).toInt()
        Log.d(
            TAG_HORZ,
            "HorizontalIntercept, ${ev.name()}, mIsScrolling:$mIsScrolling,xd=$xDiffLog, yd=$yDiffLog"
        )

        return when (ev.actionMasked) {
            // Always handle the case of the touch gesture being complete.
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Release the scroll.
                mIsScrolling = false
                false // Do not intercept touch event, let the child handle it
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsScrolling) {
                    // We're currently scrolling, so yes, intercept the
                    // touch event!
                    true
                } else {
                    // If the user has dragged her finger vertically more than
                    // the touch slop, start the scroll
                    val yDiff: Int = abs(ev.y - lastY).toInt()
                    val xDiff: Int = abs(ev.x - lastX).toInt()
                    if (xDiff > mTouchSlop && yDiff < xDiff) {
                        // Start scrolling!
                        mIsScrolling = true
                        true
                    } else {
                        false
                    }
                }
            }
            else -> {
                // In general, we don't want to intercept touch events. They should be
                // handled by the child view.
                false
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG_HORZ, "Helloooooooooooo")
        if (event.actionMasked == MotionEvent.ACTION_CANCEL || event.actionMasked == MotionEvent.ACTION_UP) {
            mIsScrolling = false
        }
        return super.onTouchEvent(event)
    }
}