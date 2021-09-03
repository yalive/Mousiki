package com.cas.musicplayer.ui.local.videos.vplayer

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.SimpleExoPlayer
import android.util.AttributeSet
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.google.android.exoplayer2.SeekParameters
import androidx.constraintlayout.widget.ConstraintSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat


class YouTubeOverlay(context: Context, private val attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), PlayerDoubleTapListener {
    constructor(context: Context) : this(context, null) {
        visibility = INVISIBLE
    }

    private var playerViewRef: Int

    private var playerView: DoubleTapPlayerView? = null
    private var player: SimpleExoPlayer? = null


    private fun initializeAttributes() {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.YouTubeOverlay, 0, 0
            )

            playerViewRef = a.getResourceId(R.styleable.YouTubeOverlay_yt_playerView, -1)

            animationDuration = a.getInt(
                R.styleable.YouTubeOverlay_yt_animationDuration, 650
            ).toLong()
            seekSeconds = a.getInt(
                R.styleable.YouTubeOverlay_yt_seekSeconds, 10
            )
            iconAnimationDuration = a.getInt(
                R.styleable.YouTubeOverlay_yt_iconAnimationDuration, 750
            ).toLong()

            arcSize = a.getDimensionPixelSize(
                R.styleable.YouTubeOverlay_yt_arcSize,
                context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size)
            ).toFloat()

            tapCircleColor = a.getColor(
                R.styleable.YouTubeOverlay_yt_tapCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)
            )
            circleBackgroundColor = a.getColor(
                R.styleable.YouTubeOverlay_yt_backgroundCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
            )

            textAppearance = a.getResourceId(
                R.styleable.YouTubeOverlay_yt_textAppearance,
                R.style.YTOSecondsTextAppearance
            )
            icon = a.getResourceId(
                R.styleable.YouTubeOverlay_yt_icon,
                R.drawable.ic_play_triangle
            )
            a.recycle()
        } else {
            arcSize = context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size).toFloat()
            tapCircleColor = ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)
            circleBackgroundColor =
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
            animationDuration = 650L
            iconAnimationDuration = 750L
            seekSeconds = 10
            textAppearance = R.style.YTOSecondsTextAppearance
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (playerViewRef != -1) playerView((parent as View).findViewById<View>(playerViewRef) as DoubleTapPlayerView)
    }


    fun playerView(playerView: DoubleTapPlayerView?): YouTubeOverlay {
        this.playerView = playerView
        return this
    }


    fun player(player: SimpleExoPlayer?): YouTubeOverlay {
        this.player = player
        return this
    }


    private var seekListener: SeekListener? = null


    fun seekListener(listener: SeekListener?): YouTubeOverlay {
        seekListener = listener
        return this
    }

    private var performListener: PerformListener? = null


    fun performListener(listener: PerformListener?): YouTubeOverlay {
        performListener = listener
        return this
    }


    var seekSeconds = 0
        private set

    fun seekSeconds(seconds: Int): YouTubeOverlay {
        seekSeconds = seconds
        return this
    }


    var tapCircleColor: Int
        get() = (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).circleColor
        private set(value) {
            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).circleColor =
                value
        }

    fun tapCircleColorRes(@ColorRes resId: Int): YouTubeOverlay {
        tapCircleColor = ContextCompat.getColor(context, resId)
        return this
    }

    fun tapCircleColorInt(@ColorInt color: Int): YouTubeOverlay {
        tapCircleColor = color
        return this
    }


    var circleBackgroundColor: Int
        get() = (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).circleBackgroundColor
        private set(value) {
            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).circleBackgroundColor =
                value
        }

    fun circleBackgroundColorRes(@ColorRes resId: Int): YouTubeOverlay {
        circleBackgroundColor = ContextCompat.getColor(context, resId)
        return this
    }

    fun circleBackgroundColorInt(@ColorInt color: Int): YouTubeOverlay {
        circleBackgroundColor = color
        return this
    }


    var animationDuration: Long
        get() = (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).animationDuration
        private set(value) {
            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).animationDuration =
                value
        }

    fun animationDuration(duration: Long): YouTubeOverlay {
        animationDuration = duration
        return this
    }

    var arcSize: Float
        get() = (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).getArcSize()
        private set(value) {
            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).setArcSize(value)
        }

    fun arcSize(@DimenRes resId: Int): YouTubeOverlay {
        arcSize = context.resources.getDimension(resId)
        return this
    }

    fun arcSize(px: Float): YouTubeOverlay {
        arcSize = px
        return this
    }

    var iconAnimationDuration: Long = 750
        get() = (findViewById<View>(R.id.seconds_view) as SecondsView).getCycleDuration()
        private set(value) {
            (findViewById<View>(R.id.seconds_view) as SecondsView).setCycleDuration(value)
            field = value
        }

    fun iconAnimationDuration(duration: Long): YouTubeOverlay {
        iconAnimationDuration = duration
        return this
    }

    var icon = 0
        get() = (findViewById<View>(R.id.seconds_view) as SecondsView).getIcon()
        private set(value) {
            (findViewById<View>(R.id.seconds_view) as SecondsView).setIcon(value)
            field = value
        }

    fun icon(@DrawableRes resId: Int): YouTubeOverlay {
        icon = resId
        return this
    }

    var textAppearance = 0
        private set(value) {
            TextViewCompat.setTextAppearance(
                (findViewById<View>(R.id.seconds_view) as SecondsView).getTextView(),
                value
            )
            field = value
        }

    fun textAppearance(@StyleRes resId: Int): YouTubeOverlay {
        textAppearance = resId
        return this
    }

    val secondsTextView: TextView
        get() = (findViewById<View>(R.id.seconds_view) as SecondsView).getTextView()

    override fun onDoubleTapStarted(posX: Float, posY: Float) {
//        if (VideoPlayerActivity.locked) return
        if ((player != null) && (player!!.currentPosition >= 0L) && (playerView != null) && (playerView!!.width > 0)) {
            if (posX >= playerView!!.width * 0.35 && posX <= playerView!!.width * 0.65) {
                if (player!!.isPlaying) {
                    player?.pause()
                } else {
                    player?.play()
                    if (playerView!!.isControllerFullyVisible) playerView?.hideController()
                }
                return
            }
        }

        //super.onDoubleTapStarted(posX, posY);
    }

    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {
//        if (VideoPlayerActivity.locked) return

        if ((player == null) || (player!!.mediaItemCount < 1) || (player!!.currentPosition < 0) || (playerView == null) || (playerView!!.width < 0)) return
        val current = player?.currentPosition
        if (current != null) {
            if (posX < playerView!!.width * 0.35 && current <= 500) return
        }

        if (current != null) {
            if (posX > playerView!!.width * 0.65 && current >= (player!!.duration - 500)) return
        }

        if (visibility != VISIBLE) {
            if (posX < playerView!!.width * 0.35 || posX > playerView!!.width * 0.65) {
                if (performListener != null) performListener!!.onAnimationStart()
                val secondsView: SecondsView = findViewById(R.id.seconds_view)
                secondsView.visibility = VISIBLE
                secondsView.start()
            } else return
        }
        if (posX < playerView!!.width * 0.35) {

            val secondsView: SecondsView = findViewById(R.id.seconds_view)
            if (secondsView.isForward()) {
                changeConstraints(false)
                secondsView.setForward(false)
                secondsView.setSeconds(0)
            }

            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).resetAnimation {
                (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).updatePosition(
                    posX,
                    posY
                )
            }
            rewinding()
        } else if (posX > playerView!!.width * 0.65) {

            val secondsView: SecondsView = findViewById(R.id.seconds_view)
            if (!secondsView.isForward()) {
                changeConstraints(true)
                secondsView.setForward(true)
                secondsView.setSeconds(0)
            }

            (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).resetAnimation {
                (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).updatePosition(
                    posX,
                    posY
                )
            }
            forwarding()
        } else {
            // Middle area tapped: do nothing
            //
            // playerView?.cancelInDoubleTapMode()
            // circle_clip_tap_view.endAnimation()
            // triangle_seconds_view.stop()
        }
    }


    private fun seekToPosition(newPosition: Long) {
        if (player == null || playerView == null) return
        player!!.setSeekParameters(SeekParameters.EXACT)

        if (newPosition <= 0) {
            player!!.seekTo(0)
            if (seekListener != null) seekListener!!.onVideoStartReached()
            return
        }

        val total = player!!.duration
        if (newPosition >= total) {
            player!!.seekTo(total)
            if (seekListener != null) seekListener!!.onVideoEndReached()
            return
        }

        // Otherwise
        playerView!!.keepInDoubleTapMode()
        player!!.seekTo(newPosition)
    }

    private fun forwarding() {
        val secondsView: SecondsView = findViewById(R.id.seconds_view)
        secondsView.setSeconds(secondsView.getSeconds() + seekSeconds)
        seekToPosition((if (player != null) player!!.currentPosition + (seekSeconds * 1000).toLong() else null)!!)
    }

    private fun rewinding() {
        val secondsView: SecondsView = findViewById(R.id.seconds_view)
        secondsView.setSeconds(secondsView.getSeconds() + seekSeconds)
        seekToPosition((if (player != null) player!!.currentPosition - (seekSeconds * 1000).toLong() else null)!!)
    }

    private fun changeConstraints(forward: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(findViewById<View>(R.id.root_constraint_layout) as ConstraintLayout)
        val secondsView: SecondsView = findViewById(R.id.seconds_view)
        if (forward) {
            constraintSet.clear(secondsView.id, ConstraintSet.START)
            constraintSet.connect(
                secondsView.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            constraintSet.clear(secondsView.id, ConstraintSet.END)
            constraintSet.connect(
                secondsView.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
        }
        secondsView.start()
        constraintSet.applyTo(findViewById<View>(R.id.root_constraint_layout) as ConstraintLayout)
    }

    interface PerformListener {
        fun onAnimationStart()
        fun onAnimationEnd()
    }

    init {
        playerViewRef = -1
        LayoutInflater.from(context).inflate(R.layout.yt_overlay, this, true)

        // Initialize UI components
        initializeAttributes()
        (findViewById<View>(R.id.seconds_view) as SecondsView).setForward(true)
        changeConstraints(true)

        // This code snippet is executed when the circle scale animation is finished
        (findViewById<View>(R.id.circle_clip_tap_view) as CircleClipTapView).performAtEnd =
            object : Runnable {
                override fun run() {
                    if (performListener != null) performListener?.onAnimationEnd()
                    val secondsView: SecondsView = findViewById(R.id.seconds_view)
                    secondsView.visibility = INVISIBLE
                    secondsView.setSeconds(0)
                    secondsView.stop()
                }
            }
    }
}