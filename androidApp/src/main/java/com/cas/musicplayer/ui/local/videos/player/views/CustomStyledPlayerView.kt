package com.cas.musicplayer.ui.local.videos.player.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.pixelsToDp
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.math.abs

/**
 **********************************
 * Created by user on 9/4/21.
 ***********************************
 */
class CustomStyledPlayerView(context: Context, attrs: AttributeSet?) :
    StyledPlayerView(context, attrs),
    GestureDetector.OnGestureListener, OnScaleGestureListener {

    var locked = false
    private var restorePlayState = false
    private var canScale = true
    private var isHandledLongPress = false
    private var handleTouch = false
    var restoreControllerTimeout = false
    var shortControllerTimeout = false

    private var seekStart: Long = 0
    private var seekChange: Long = 0
    private var seekMax: Long = 0
    private var canBoostVolume = false
    private var canSetAutoBrightness = false

    private var gestureScrollY = 0f
    private var gestureScrollX = 0f

    var currentBrightnessLevel = -1

    var boostLevel = 0

    private val IGNORE_BORDER = context.dpToPixel(24F)
    private val SCROLL_STEP = context.dpToPixel(16F)
    private val SCROLL_STEP_SEEK = context.dpToPixel(8F)

    private var exoErrorMessage: TextView? = null
    private var exoProgress: View? = null

    private var mDetector: GestureDetectorCompat? = null
    private var gestureOrientation: Orientation = Orientation.UNKNOWN
    private var mScaleDetector: ScaleGestureDetector? = null
    var loudnessEnhancer: LoudnessEnhancer? = null
    private var mAudioManager: AudioManager? = null
    private var mScaleFactor = 1f
    private var mScaleFactorFit = 0f

    var systemGestureExclusionRect = Rect()


    private var gestureDetector: GestureDetectorCompat? = null
    private var gestureListener: DoubleTapGestureListener? = null
    private var controller: PlayerDoubleTapListener? = null

    private var controllerRef = 0

    var isDoubleTapEnabled = false
    private var doubleTapDelay: Long = 0

    init {

        controllerRef = -1
        gestureListener = DoubleTapGestureListener(this)
        gestureDetector = GestureDetectorCompat(context, gestureListener)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CustomStyledPlayerView, 0, 0)
            controllerRef = a.getResourceId(R.styleable.CustomStyledPlayerView_dtpv_controller, -1)
            a.recycle()
        }
        isDoubleTapEnabled = true
        doubleTapDelay = 700L

        mDetector = GestureDetectorCompat(context, this)
        mScaleDetector = ScaleGestureDetector(context, this)
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        exoErrorMessage = findViewById(R.id.exo_error_message)
        exoProgress = findViewById(R.id.exo_progress)
        exoErrorMessage?.setOnClickListener { v: View? ->
            if (locked) {
                locked = false
                showText(
                    "",
                    MESSAGE_TIMEOUT_LONG
                )
                setIconLock(false)
            }
        }
    }

    private fun setController(value: PlayerDoubleTapListener) {
        gestureListener?.controls = value
        controller = value
    }

    fun getDoubleTapDelay(): Long? {
        return gestureListener?.doubleTapDelay
    }

    fun setDoubleTapDelay(value: Long) {
        gestureListener?.doubleTapDelay = value
        doubleTapDelay = value
    }

    fun isInDoubleTapMode(): Boolean? {
        return gestureListener?.isDoubleTapping
    }

    fun keepInDoubleTapMode() {
        gestureListener?.keepInDoubleTapMode()
    }

    fun cancelInDoubleTapMode() {
        gestureListener?.cancelInDoubleTapMode()
    }


    open fun setScale(scale: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val videoSurfaceView = videoSurfaceView
            videoSurfaceView?.scaleX = scale
            videoSurfaceView?.scaleY = scale
        }
    }

    fun tap(): Boolean {
        if (locked) {
            showText("", MESSAGE_TIMEOUT_LONG)
            setIconLock(true)
            return true
        }
        if (!isControllerFullyVisible || player?.isPlaying!!) {
            return true
        }
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (controllerRef != -1) {
            try {
                val view = (parent as View).findViewById<View>(this.controllerRef)
                if (view is PlayerDoubleTapListener) {
                    setController(view as PlayerDoubleTapListener)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (restoreControllerTimeout) {
            controllerShowTimeoutMs = CONTROLLER_TIMEOUT
            restoreControllerTimeout = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && gestureOrientation == Orientation.UNKNOWN) mScaleDetector?.onTouchEvent(
            ev
        )
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                removeCallbacks(textClearRunnable)
                handleTouch = true
            }
            MotionEvent.ACTION_UP -> if (handleTouch) {
                if (gestureOrientation == Orientation.HORIZONTAL) {
                    setCustomErrorMessage(null)
                } else {
                    postDelayed(
                        textClearRunnable,
                        if (isHandledLongPress) MESSAGE_TIMEOUT_LONG else MESSAGE_TIMEOUT_TOUCH.toLong()
                    )
                }
                if (restorePlayState) {
                    restorePlayState = false
                    player?.play()
                }
                controllerAutoShow = true
            }
        }

        if (handleTouch) mDetector!!.onTouchEvent(ev)

        if (isDoubleTapEnabled) {
            val consumed = gestureDetector?.onTouchEvent(ev)

            return if (!consumed!!) super.onTouchEvent(ev) else true
        }

        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        gestureScrollY = 0f
        gestureScrollX = 0f
        gestureOrientation = Orientation.UNKNOWN
        isHandledLongPress = false

        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent?,
        motionEvent1: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (mScaleDetector!!.isInProgress || player == null || locked) return false

        if (motionEvent?.y!! < IGNORE_BORDER || motionEvent.x < IGNORE_BORDER || motionEvent.y > height - IGNORE_BORDER || motionEvent.x > width - IGNORE_BORDER) return false

        if (gestureScrollY == 0f || gestureScrollX == 0f) {
            gestureScrollY = 0.0001f
            gestureScrollX = 0.0001f
            return false
        }

        if (gestureOrientation == Orientation.HORIZONTAL || gestureOrientation == Orientation.UNKNOWN) {
            gestureScrollX += distanceX
            if (Math.abs(gestureScrollX) > SCROLL_STEP || gestureOrientation == Orientation.HORIZONTAL && Math.abs(
                    gestureScrollX
                ) > SCROLL_STEP_SEEK
            ) {
                // Do not show controller if not already visible
                controllerAutoShow = false
                if (gestureOrientation == Orientation.UNKNOWN) {
                    if (player?.isPlaying!!) {
                        restorePlayState = true
                        player?.pause()
                    }
                    clearIcon()
                    seekStart = player?.currentPosition!!
                    seekChange = 0L
                    seekMax = player?.duration!!
                }
                gestureOrientation = Orientation.HORIZONTAL
                val position: Long
                val distanceDiff =
                    0.5f.coerceAtLeast(
                        abs(context.pixelsToDp(distanceX) / 4).coerceAtMost(10f)
                    )
                if (gestureScrollX > 0) {
                    if (seekStart + seekChange - SEEK_STEP * distanceDiff >= 0) {
                        player?.seekToPrevious()
                        seekChange -= (SEEK_STEP * distanceDiff).toLong()
                        position = seekStart + seekChange
                        player?.seekTo(position)
                    }
                } else {
                    player?.seekToNext()
                    if (seekMax == C.TIME_UNSET) {
                        seekChange += (SEEK_STEP * distanceDiff).toLong()
                        position = seekStart + seekChange
                        player?.seekTo(position)
                    } else if (seekStart + seekChange + SEEK_STEP < seekMax) {
                        seekChange += (SEEK_STEP * distanceDiff).toLong()
                        position = seekStart + seekChange
                        player?.seekTo(position)
                    }
                }
                setCustomErrorMessage(formatMilisSign(seekChange))
                gestureScrollX = 0.0001f
            }
        }

        if (gestureOrientation == Orientation.VERTICAL || gestureOrientation == Orientation.UNKNOWN) {
            gestureScrollY += distanceY
            if (Math.abs(gestureScrollY) > SCROLL_STEP) {
                if (gestureOrientation == Orientation.UNKNOWN) {
                    canBoostVolume = isVolumeMax(mAudioManager)
                    canSetAutoBrightness = currentBrightnessLevel <= 0
                }
                gestureOrientation = Orientation.VERTICAL
                if (motionEvent.x < (width / 2).toFloat()) {
                    changeBrightness(
                        this,
                        gestureScrollY > 0,
                        canSetAutoBrightness
                    )
                } else {
                    adjustVolume(mAudioManager, gestureScrollY > 0, canBoostVolume)
                }
                gestureScrollY = 0.0001f
            }
        }

        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        if (locked || player != null && player!!.isPlaying) {
            locked = !locked
            isHandledLongPress = true
            showText("", MESSAGE_TIMEOUT_LONG)
            setIconLock(locked)
            if (locked) {
                hideController()
            }
        }
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onScale(scaleGestureDetector: ScaleGestureDetector?): Boolean {
        if (locked) return false

        if (canScale) {
            val previousScaleFactor: Float = mScaleFactor
            mScaleFactor *= scaleGestureDetector?.scaleFactor!!
            mScaleFactor = 0.25f.coerceAtLeast(mScaleFactor.coerceAtMost(2.0f))
            if (isCrossingThreshold(previousScaleFactor, mScaleFactor, 1.0f) ||
                isCrossingThreshold(previousScaleFactor, mScaleFactor, mScaleFactorFit)
            ) performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            setScale(mScaleFactor)
            restoreSurfaceView()
            clearIcon()
            setCustomErrorMessage("${(mScaleFactor * 100).toInt()} %")
            return true
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        if (locked) return false

        mScaleFactor = videoSurfaceView!!.scaleX
        if (resizeMode != AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
            canScale = false
            setAspectRatioListener { _: Float, _: Float, _: Boolean ->
                setAspectRatioListener(null)
                mScaleFactorFit = getScaleFit()
                mScaleFactor = mScaleFactorFit
                canScale = true
            }
            videoSurfaceView!!.alpha = 0f
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            mScaleFactorFit = getScaleFit()
            canScale = true
        }
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        if (locked) return
        restoreSurfaceView()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (Build.VERSION.SDK_INT >= 29) {
            exoProgress?.getGlobalVisibleRect(systemGestureExclusionRect)
            systemGestureExclusionRect.left = left
            systemGestureExclusionRect.right = right
            systemGestureExclusionRects = listOf(systemGestureExclusionRect)
        }
    }

    private val textClearRunnable = Runnable {
        setCustomErrorMessage(null)
        clearIcon()
    }


    fun showText(text: String?, timeout: Long = MESSAGE_TIMEOUT_LONG) {
        removeCallbacks(this.textClearRunnable)
        clearIcon()
        setCustomErrorMessage(text)
        postDelayed(this.textClearRunnable, timeout)
    }

    fun clearIcon() {
        exoErrorMessage?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        setHighlight(false)
    }

    private fun setIconLock(locked: Boolean) {
        exoErrorMessage!!.setCompoundDrawablesWithIntrinsicBounds(
            if (locked) R.drawable.ic_lock_24dp else R.drawable.ic_lock_open_24dp,
            0,
            0,
            0
        )
    }

     private fun setIconVolume(volumeActive: Boolean) {
        exoErrorMessage!!.setCompoundDrawablesWithIntrinsicBounds(
            if (volumeActive) R.drawable.ic_volume_up_24dp else R.drawable.ic_volume_off_24dp,
            0,
            0,
            0
        )
    }

     private fun setHighlight(active: Boolean) {
        if (active) exoErrorMessage!!.background.setTint(Color.RED) else exoErrorMessage!!.background.setTintList(
            null
        )
    }

     private fun setIconBrightness() {
        exoErrorMessage!!.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_brightness_medium_24,
            0,
            0,
            0
        )
    }

     private fun setIconBrightnessAuto() {
        exoErrorMessage!!.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_brightness_auto_24dp,
            0,
            0,
            0
        )
    }

     private fun changeBrightness(
        playerView: CustomStyledPlayerView,
        increase: Boolean,
        canSetAuto: Boolean
    ) {
        val newBrightnessLevel: Int =
            if (increase) currentBrightnessLevel + 1 else currentBrightnessLevel - 1
        if (canSetAuto && newBrightnessLevel < 0) currentBrightnessLevel =
            -1 else if (newBrightnessLevel in 0..30) currentBrightnessLevel =
            newBrightnessLevel
        if (currentBrightnessLevel == -1 && canSetAuto)
            setScreenBrightness(
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            )
        else if (currentBrightnessLevel != -1)
            setScreenBrightness(
                levelToBrightness(currentBrightnessLevel)
            )
        if (currentBrightnessLevel == -1 && canSetAuto) {
            playerView.setIconBrightnessAuto()
            playerView.setCustomErrorMessage("")
        } else {
            playerView.setIconBrightness()
            playerView.setCustomErrorMessage(" $currentBrightnessLevel")
        }
    }

     private fun setScreenBrightness(brightness: Float) {
        if (context is Activity) {
            val lp: WindowManager.LayoutParams = (context as Activity).window.attributes
            lp.screenBrightness = brightness
            (context as Activity).window.attributes = lp
        }
    }

     private fun levelToBrightness(level: Int): Float {
        val d = 0.064 + 0.936 / 30.toDouble() * level.toDouble()
        return (d * d).toFloat()
    }

     fun formatMilisSign(time: Long): String? {
        return if (time > -1000 && time < 1000) formatMilis(time) else (if (time < 0) "−" else "+") + formatMilis(
            time
        )
    }

     private fun formatMilis(time: Long): String? {
        val totalSeconds = Math.abs(time.toInt() / 1000)
        val seconds = totalSeconds % 60
        val minutes = totalSeconds % 3600 / 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format(
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds
        ) else String.format("%02d:%02d", minutes, seconds)
    }

     private fun isVolumeMax(audioManager: AudioManager?): Boolean {
        return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == audioManager?.getStreamMaxVolume(
            AudioManager.STREAM_MUSIC
        )
    }

     private fun isVolumeMin(audioManager: AudioManager?): Boolean {
        val min =
            if (Build.VERSION.SDK_INT >= 28) audioManager?.getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0
        return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == min
    }

     private fun adjustVolume(
        audioManager: AudioManager?,
        raise: Boolean,
        canBoost: Boolean
    ) {
        var canBoost = canBoost
        val volume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        val volumeMax = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var volumeActive = volume != 0

        // Handle volume changes outside the app (lose boost if volume is not maxed out)
        if (volume != volumeMax) {
            boostLevel = 0
        }
        if (loudnessEnhancer == null) canBoost = false
        if (!(volume == volumeMax && !(boostLevel === 0 && !raise))) {
            loudnessEnhancer?.enabled = false
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (raise) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
            val volumeNew = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (raise && volume == volumeNew && !isVolumeMin(audioManager)) {
                audioManager?.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE or AudioManager.FLAG_SHOW_UI
                )
            } else {
                volumeActive = volumeNew != 0
                setCustomErrorMessage(if (volumeActive) " $volumeNew" else "")
            }
        } else {
            if (canBoost && raise && boostLevel < 10) boostLevel++ else if (!raise && boostLevel > 0) boostLevel--
            try {
                loudnessEnhancer?.setTargetGain(boostLevel * 200)
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            setCustomErrorMessage(" " + (volumeMax!! + boostLevel))
        }
        setIconVolume(volumeActive)
        loudnessEnhancer?.enabled = boostLevel > 0
        setHighlight(boostLevel > 0)
    }

    private fun isCrossingThreshold(val1: Float, val2: Float, threshold: Float): Boolean {
        return val1 < threshold && val2 >= threshold || val1 > threshold && val2 <= threshold
    }

    private fun restoreSurfaceView() {
        if (videoSurfaceView!!.alpha != 1f) {
            videoSurfaceView!!.alpha = 1f
        }
    }

    private fun getScaleFit(): Float {
        return (height.toFloat() / videoSurfaceView!!.height.toFloat()).coerceAtMost(width.toFloat() / videoSurfaceView!!.width.toFloat())
    }

    private class DoubleTapGestureListener(private val rootView: CustomStyledPlayerView) :
        GestureDetector.SimpleOnGestureListener() {
        private val mHandler: Handler = Handler()
        private val mRunnable: Runnable
        var controls: PlayerDoubleTapListener? = null
        var isDoubleTapping = false
        var doubleTapDelay: Long

        /**
         * Resets the timeout to keep in double tap mode.
         *
         * Called once in [PlayerDoubleTapListener.onDoubleTapStarted]. Needs to be called
         * from outside if the double tap is customized / overridden to detect ongoing taps
         */
        fun keepInDoubleTapMode() {
            isDoubleTapping = true
            mHandler.removeCallbacks(mRunnable)
            mHandler.postDelayed(mRunnable, doubleTapDelay)
        }

        /**
         * Cancels double tap mode instantly by calling [PlayerDoubleTapListener.onDoubleTapFinished]
         */
        fun cancelInDoubleTapMode() {
            mHandler.removeCallbacks(mRunnable)
            isDoubleTapping = false
            controls?.onDoubleTapFinished()
        }

        override fun onDown(e: MotionEvent): Boolean {
            // Used to override the other methods
            if (isDoubleTapping) {
                controls?.onDoubleTapProgressDown(e.x, e.y)
                return true
            }
            return super.onDown(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isDoubleTapping) {
                controls?.onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onSingleTapUp(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isDoubleTapping) return true
            return rootView.tap()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // First tap (ACTION_DOWN) of both taps
            if (!isDoubleTapping) {
                isDoubleTapping = true
                keepInDoubleTapMode()
                controls?.onDoubleTapStarted(e.x, e.y)
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

        init {
            mRunnable = Runnable {
                isDoubleTapping = false
                if (controls != null) controls!!.onDoubleTapFinished()
            }
            doubleTapDelay = 650L
        }
    }

    companion object {
        const val SEEK_STEP: Long = 1000
        const val MESSAGE_TIMEOUT_TOUCH = 400L
        const val MESSAGE_TIMEOUT_LONG = 1400L
        const val CONTROLLER_TIMEOUT = 3500
    }

}

enum class Orientation {
    HORIZONTAL, VERTICAL, UNKNOWN
}