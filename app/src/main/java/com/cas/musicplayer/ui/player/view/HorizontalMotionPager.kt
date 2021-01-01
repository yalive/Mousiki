package com.cas.musicplayer.ui.player.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import com.cas.musicplayer.ui.player.name
import kotlin.math.abs

const val TAG_HORZ = "HorizontalMotion"

class HorizontalMotionPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private var mIsScrolling = false
    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var lastY = 0f
    private var lastX = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG_NEW, "onInterceptTouchEvent child: ${event.name()}")
        val parentMotion = parent as? SingleViewTouchableMotionLayout ?: return false
        if (parentMotion.mIsScrolling || (parentMotion.progress != 0f && parentMotion.progress != 1.0f)) {
            Log.d(
                TAG_HORZ,
                "onInterceptTouchEvent pager blocked: ${event.name()}, progress = ${parentMotion.progress}"
            )
            return false
        } else {
            Log.d(
                TAG_HORZ,
                "onInterceptTouchEvent pager noooon blocked: ${event.name()}, progress = ${parentMotion.progress}"
            )
        }
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            lastY = event.y
            lastX = event.x
        }

        return when (event.actionMasked) {
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
                    val yDiff: Int = abs(event.y - lastY).toInt()
                    val xDiff: Int = abs(event.x - lastX).toInt()
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
        Log.d(TAG_NEW, "onTouchEvent child: ${event.name()}")
        val parentMotion = parent as? SingleViewTouchableMotionLayout ?: return false
        if (parentMotion.mIsScrolling || (parentMotion.progress != 0f && parentMotion.progress != 1.0f)) {
            Log.d(
                TAG_HORZ,
                "onTouchEvent pager blocked: ${event.name()}, progress = ${parentMotion.progress}"
            )
            mIsScrolling = false
            transitionToStart()
            return false
        } else {
            Log.d(
                TAG_HORZ,
                "onTouchEvent pager noooon blocked: ${event.name()}, progress = ${parentMotion.progress}"
            )
        }

        if (event.actionMasked == MotionEvent.ACTION_CANCEL || event.actionMasked == MotionEvent.ACTION_UP) {
            mIsScrolling = false
        }
        return super.onTouchEvent(event)
    }
}