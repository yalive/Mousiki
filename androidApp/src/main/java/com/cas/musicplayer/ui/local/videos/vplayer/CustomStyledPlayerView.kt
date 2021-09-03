package com.cas.musicplayer.ui.local.videos.vplayer

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.dpToPx
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.isTvBox
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.showText
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.pxToDp
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.formatMilisSign
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.isVolumeMax
import com.cas.musicplayer.ui.local.videos.vplayer.Utils.adjustVolume
import com.google.android.exoplayer2.ui.StyledPlayerView
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
import android.util.AttributeSet
import android.view.*
import com.google.android.exoplayer2.SeekParameters
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.utils.BrightnessUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * *********************************
 * Created by user on 8/29/21.
 * **********************************
 */
open class CustomStyledPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StyledPlayerView(context, attrs, defStyleAttr), GestureDetector.OnGestureListener,
    OnScaleGestureListener {

    private val mDetector: GestureDetectorCompat = GestureDetectorCompat(context, this)
    private var gestureOrientation = Orientation.UNKNOWN
    private var gestureScrollY = 0f
    private var gestureScrollX = 0f
    private var handleTouch = false
    private var seekStart: Long = 0
    private var seekChange: Long = 0
    private var seekMax: Long = 0
    private var canBoostVolume = false
    private var canSetAutoBrightness = false
    private val IGNORE_BORDER = dpToPx(24).toFloat()
    private val SCROLL_STEP = dpToPx(16).toFloat()
    private val SCROLL_STEP_SEEK = dpToPx(8).toFloat()
    private val SEEK_STEP: Long = 1000
    private var restorePlayState = false
    private var canScale = true
    private var isHandledLongPress = false
    private val mScaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private var mScaleFactor = 1f
    private var mScaleFactorFit = 0f
    var systemGestureExclusionRect = Rect()

    var restoreControllerTimeout = false
    var controllerVisibleFully = false
    var controllerVisible = false
    var currentBrightnessLevel = -1

    var locked = false

    var player: SimpleExoPlayer? = null

    var loudnessEnhancer: LoudnessEnhancer? = null

    @JvmField
    val textClearRunnable = Runnable {
        setCustomErrorMessage(null)
        clearIcon()
    }
    private val mAudioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val exoErrorMessage: TextView = findViewById(R.id.exo_error_message)
    private val exoProgress: View = findViewById(R.id.exo_progress)


    fun clearIcon() {
        exoErrorMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        setHighlight(false)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (restoreControllerTimeout) {
            controllerShowTimeoutMs = VideoPlayerActivity.CONTROLLER_TIMEOUT
            restoreControllerTimeout = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && gestureOrientation == Orientation.UNKNOWN) mScaleDetector.onTouchEvent(
            ev
        )
        when (ev.actionMasked) {
            /*MotionEvent.ACTION_DOWN -> handleTouch =
                if (snackbar.isShown) {
                    //snackbar.dismiss()
                    false
                } else {
                    removeCallbacks(textClearRunnable)
                    true
                }*/

            MotionEvent.ACTION_UP -> if (handleTouch) {
                if (gestureOrientation == Orientation.HORIZONTAL) {
                    setCustomErrorMessage(null)
                } else {
                    postDelayed(
                        textClearRunnable,
                        if (isHandledLongPress) MESSAGE_TIMEOUT_LONG.toLong() else MESSAGE_TIMEOUT_TOUCH.toLong()
                    )
                }
                if (restorePlayState) {
                    restorePlayState = false
                    player?.play()
                }
                controllerAutoShow = true
            }
        }
        if (handleTouch) mDetector.onTouchEvent(ev)

        // Handle all events to avoid conflict with internal handlers
        return true
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        gestureScrollY = 0f
        gestureScrollX = 0f
        gestureOrientation = Orientation.UNKNOWN
        isHandledLongPress = false
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    fun tap(): Boolean {
        if (locked) {
            showText(this, "", MESSAGE_TIMEOUT_LONG.toLong())
            setIconLock(true)
            return true
        }
        if (!controllerVisibleFully) {
            showController()
            return true
        } else if (player!!.isPlaying) {
            hideController()
            return true
        }
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (mScaleDetector.isInProgress || locked) return false

        // Exclude edge areas
        if (motionEvent.y < IGNORE_BORDER || motionEvent.x < IGNORE_BORDER || motionEvent.y > height - IGNORE_BORDER || motionEvent.x > width - IGNORE_BORDER) return false
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
                    if (player!!.isPlaying) {
                        restorePlayState = true
                        player?.pause()
                    }
                    clearIcon()
                    seekStart = player!!.currentPosition
                    seekChange = 0L
                    seekMax = player!!.duration
                }
                gestureOrientation = Orientation.HORIZONTAL
                val position: Long
                val distanceDiff = Math.max(0.5f, Math.min(Math.abs(pxToDp(distanceX) / 4), 10f))
                    if (gestureScrollX > 0) {
                        if (seekStart + seekChange - SEEK_STEP * distanceDiff >= 0) {
                            player?.setSeekParameters(SeekParameters.PREVIOUS_SYNC)
                            seekChange -= (SEEK_STEP * distanceDiff).toLong()
                            position = seekStart + seekChange
                            player?.seekTo(position)
                        }
                    } else {
                        player?.setSeekParameters(SeekParameters.NEXT_SYNC)
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

        // LEFT = Brightness  |  RIGHT = Volume
        if (gestureOrientation == Orientation.VERTICAL || gestureOrientation == Orientation.UNKNOWN) {
            gestureScrollY += distanceY
            if (Math.abs(gestureScrollY) > SCROLL_STEP) {
                if (gestureOrientation == Orientation.UNKNOWN) {
                    canBoostVolume = isVolumeMax(mAudioManager)
                    canSetAutoBrightness =
                        currentBrightnessLevel <= 0
                }
                gestureOrientation = Orientation.VERTICAL
                if (motionEvent.x < (width / 2).toFloat()) {
                    changeBrightness(
                        this,
                        gestureScrollY > 0,
                        canSetAutoBrightness
                    )
                } else {
                    adjustVolume(mAudioManager, this, gestureScrollY > 0, canBoostVolume,loudnessEnhancer!!,0)
                }
                gestureScrollY = 0.0001f
            }
        }
        return true
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        if (locked || player != null && player!!.isPlaying) {
            locked = !locked
            isHandledLongPress = true
            showText(this, "", MESSAGE_TIMEOUT_LONG.toLong())
            setIconLock(locked)
            if (locked && controllerVisible) {
                hideController()
            }
        }
    }

    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
        if (locked) return false
        if (canScale) {
            val previousScaleFactor = mScaleFactor
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.25f, Math.min(mScaleFactor, 2.0f))
            if (isCrossingThreshold(previousScaleFactor, mScaleFactor, 1.0f) ||
                isCrossingThreshold(previousScaleFactor, mScaleFactor, mScaleFactorFit)
            ) performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            setScale(mScaleFactor)
            restoreSurfaceView()
            clearIcon()
            setCustomErrorMessage("${(mScaleFactor * 100)} %")
            return true
        }
        return false
    }

    private fun isCrossingThreshold(val1: Float, val2: Float, threshold: Float): Boolean {
        return val1 < threshold && val2 >= threshold || val1 > threshold && val2 <= threshold
    }

    override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
        if (locked) return false
        mScaleFactor = videoSurfaceView!!.scaleX
        if (resizeMode != AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
            canScale = false
            setAspectRatioListener { targetAspectRatio: Float, naturalAspectRatio: Float, aspectRatioMismatch: Boolean ->
                setAspectRatioListener(null)
                mScaleFactorFit = scaleFit
                mScaleFactor = mScaleFactorFit
                canScale = true
            }
            videoSurfaceView!!.alpha = 0f
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            mScaleFactorFit = scaleFit
            canScale = true
        }
        return true
    }

    override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {
        if (locked) return
        restoreSurfaceView()
    }

    private fun restoreSurfaceView() {
        if (videoSurfaceView!!.alpha != 1f) {
            videoSurfaceView!!.alpha = 1f
        }
    }

    private val scaleFit: Float
        private get() = Math.min(
            height.toFloat() / videoSurfaceView!!.height.toFloat(),
            width.toFloat() / videoSurfaceView!!.width.toFloat()
        )

    private enum class Orientation {
        HORIZONTAL, VERTICAL, UNKNOWN
    }

    fun setIconVolume(volumeActive: Boolean) {
        exoErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
            if (volumeActive) R.drawable.ic_volume_up_24dp else R.drawable.ic_volume_off_24dp,
            0,
            0,
            0
        )
    }

    fun setHighlight(active: Boolean) {
        if (active) exoErrorMessage.background.setTint(Color.RED) else exoErrorMessage.background.setTintList(
            null
        )
    }

    fun setIconBrightness() {
        exoErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_brightness_medium_24,
            0,
            0,
            0
        )
    }

    fun setIconBrightnessAuto() {
        exoErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_brightness_auto_24dp,
            0,
            0,
            0
        )
    }

    fun setIconLock(locked: Boolean) {
        exoErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
            if (locked) R.drawable.ic_lock_24dp else R.drawable.ic_lock_open_24dp,
            0,
            0,
            0
        )
    }

    fun setScale(scale: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val videoSurfaceView = videoSurfaceView
            videoSurfaceView!!.scaleX = scale
            videoSurfaceView.scaleY = scale
            //videoSurfaceView.animate().setStartDelay(0).setDuration(0).scaleX(scale).scaleY(scale).start();
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (Build.VERSION.SDK_INT >= 29) {
            exoProgress.getGlobalVisibleRect(systemGestureExclusionRect)
            systemGestureExclusionRect.left = left
            systemGestureExclusionRect.right = right
            systemGestureExclusionRects = listOf(systemGestureExclusionRect)
        }
    }

    companion object {
        const val MESSAGE_TIMEOUT_TOUCH = 400
        const val MESSAGE_TIMEOUT_KEY = 800
        const val MESSAGE_TIMEOUT_LONG = 1400
    }

    init {
        if (!isTvBox(getContext())) {
            exoErrorMessage.setOnClickListener { v: View? ->
                if (locked) {
                    locked = false
                    showText(this@CustomStyledPlayerView, "", MESSAGE_TIMEOUT_LONG.toLong())
                    setIconLock(false)
                }
            }
        }
    }

    private fun changeBrightness(
        playerView: CustomStyledPlayerView,
        increase: Boolean,
        canSetAuto: Boolean
    ) {
        val newBrightnessLevel =
            if (increase) currentBrightnessLevel + 1 else currentBrightnessLevel - 1

        if (canSetAuto && newBrightnessLevel < 0)
            currentBrightnessLevel = -1
        else if (newBrightnessLevel in 0..30)
            currentBrightnessLevel = newBrightnessLevel
        if (currentBrightnessLevel == -1 && canSetAuto)
            BrightnessUtils.setSystemScreenBrightness(
                context,
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE.toInt()
            )
        else if (currentBrightnessLevel != -1)
            BrightnessUtils.setSystemScreenBrightness(
                context,
                levelToBrightness(currentBrightnessLevel).toInt()
            )
        if (currentBrightnessLevel == -1 && canSetAuto) {
            playerView.setIconBrightnessAuto()
            playerView.setCustomErrorMessage("")
        } else {
            playerView.setIconBrightness()
            playerView.setCustomErrorMessage(" $currentBrightnessLevel")
        }
    }

    fun levelToBrightness(level: Int): Int {
        val d = 0.064 + 0.936 / 30.toDouble() * level.toDouble()
        return (d * d).toInt()
    }
}