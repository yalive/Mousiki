package com.cas.musicplayer.ui.player.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.player.name
import com.cas.musicplayer.ui.player.xDistanceTo
import com.cas.musicplayer.ui.player.yDistanceTo
import kotlin.math.abs


const val TAG_MOTION = "SingleViewTouchableMoti"

class SingleViewTouchableMotionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.miniPlayerView)
    }
    private val viewRect = Rect()
    private var touchStarted = false

    var mIsScrolling = false
        private set

    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var lastY = 0f
    private var lastX = 0f

    private var lastDownTime: Long = 0

    init {
        setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                touchStarted = false
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                super.onTransitionStarted(motionLayout, startId, endId)
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                super.onTransitionChange(motionLayout, startId, endId, progress)
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        Log.d(
            TAG_MOTION,
            "onInterceptTouchEvent **Main**: ${event.name()}, progress = ${progress}"
        )

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            lastY = event.y
            lastX = event.x
            lastDownTime = System.currentTimeMillis()
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
                    if (yDiff > mTouchSlop && yDiff > xDiff) {

                        if (progress > 0) {
                            if ((event.y - lastY).toInt() > 0) {
                                // Start scrolling!
                                mIsScrolling = true
                                true
                            } else {
                                // Do not start up because already expanded
                                mIsScrolling = false
                                false
                            }
                        } else {
                            // Start scrolling!
                            mIsScrolling = true
                            true
                        }

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
        Log.d(
            TAG_MOTION,
            "onTouchEvent Main: ${event.name()}, progress = ${progress},touchStarted=$touchStarted"
        )

        if (event.actionMasked == MotionEvent.ACTION_CANCEL
            || event.actionMasked == MotionEvent.ACTION_UP
            || event.actionMasked == MotionEvent.ACTION_POINTER_UP
        ) {
            var clicked = false
            if (touchStarted && isAClick(
                    lastX,
                    event.x,
                    lastY,
                    event.y
                ) && event.actionMasked == MotionEvent.ACTION_UP
            ) {
                Log.d(TAG_MOTION, "on click")
                clicked = true
                transitionToState(R.id.expanded)
            }
            touchStarted = false
            mIsScrolling = false
            if (clicked) return true
        }

        if (event.action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted = false
            transitionToEnd()
            // TODO: Return to be reviewed
            return true
        }

        if (startState == R.id.collapsed && endState == R.id.expanded) {
            // Consume touch on the whole screen
            val yDiff = event.yDistanceTo(lastY).toInt()
            val xDiff = event.xDistanceTo(lastX).toInt()
            if (event.action == MotionEvent.ACTION_DOWN && progress > 0f) {
                return super.onTouchEvent(event)
            }
            if (yDiff > mTouchSlop && yDiff > xDiff) {
                return super.onTouchEvent(event)
            } else if (yDiff < xDiff) {
                return false
            }
        }

        if (event.xDistanceTo(lastX) > event.yDistanceTo(lastY)) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchStarted = false
                return super.onTouchEvent(event)
            }
        }

        if (!touchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            touchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())

            if (touchStarted && event.action == MotionEvent.ACTION_UP) {
                Log.d(TAG_MOTION, "on UP")
            }
        }
        return (touchStarted && super.onTouchEvent(event))
    }

    private fun isAClick(startX: Float?, endX: Float, startY: Float?, endY: Float): Boolean {
        if (System.currentTimeMillis() - lastDownTime > 200) return false
        if (startX == null || startY == null) return false
        val differenceX = abs(startX - endX)
        val differenceY = abs(startY - endY)
        return !/* =5 */(differenceX > 200 || differenceY > 200)
    }


    private fun stateName() {

    }
}