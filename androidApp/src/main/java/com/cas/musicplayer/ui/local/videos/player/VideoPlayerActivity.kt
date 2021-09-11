package com.cas.musicplayer.ui.local.videos.player

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Icon
import android.media.audiofx.AudioEffect
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Rational
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import androidx.lifecycle.Lifecycle
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ActivityVideoPlayerBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.ui.local.videos.player.views.CustomDefaultTimeBar
import com.cas.musicplayer.ui.local.videos.player.views.CustomStyledPlayerView
import com.cas.musicplayer.ui.local.videos.queue.VideosQueueFragment
import com.cas.musicplayer.utils.SystemSettings
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.ui.TimeBar.OnScrubListener
import java.util.*
import kotlin.math.abs


/**
 **********************************
 * Created by user on 9/4/21.
 ***********************************
 */
class VideoPlayerActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityVideoPlayerBinding.inflate(layoutInflater)
    }

    private val viewModel: VideoPlayerViewModel by viewModel { Injector.videoPlayerViewModel }

    private var mPictureInPictureParamsBuilder: Any? = null

    private val rationalLimitWide = Rational(239, 100)
    private val rationalLimitTall = Rational(100, 239)

    private var buttonPiP: ImageButton? = null
    private var buttonAspectRatio: ImageButton? = null
    private var buttonRotation: ImageButton? = null
    private var controlView: StyledPlayerControlView? = null
    private var titleView: TextView? = null
    private var timeBar: CustomDefaultTimeBar? = null

    private var titleViewPadding = 0
    var frameRendered = false

    private var isScrubbing = false
    private var restorePlayState = false
    private var scrubbingNoticeable = false
    private var scrubbingStart: Long = 0

    private val playbackStateListener: Player.Listener = playbackStateListener()

    private var mReceiver: BroadcastReceiver? = null

    private var currentOrientation = Orientation.PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.videoView.player = viewModel.player
        viewBinding.videoView.player?.addListener(playbackStateListener)
        viewBinding.videoView.loudnessEnhancer = viewModel.loudnessEnhancer

        viewBinding.videoView.setShowNextButton(false)
        viewBinding.videoView.setShowPreviousButton(false)
        viewBinding.videoView.setShowFastForwardButton(false)
        viewBinding.videoView.setShowRewindButton(false)

        viewBinding.videoView.controllerHideOnTouch = true
        viewBinding.videoView.controllerAutoShow = true
        viewBinding.videoView.isDoubleTapEnabled = true

        setupPiPButton()
        setupRatioButton()
        setupRotate()

        val exoBasicControls: LinearLayout =
            viewBinding.videoView.findViewById(R.id.exo_basic_controls)
        val exoSubtitle = exoBasicControls.findViewById<ImageButton>(R.id.exo_subtitle)
        exoBasicControls.removeView(exoSubtitle)

        val exoSettings = exoBasicControls.findViewById<ImageButton>(R.id.exo_settings)
        exoBasicControls.removeView(exoSettings)

        val horizontalScrollView =
            layoutInflater.inflate(R.layout.controls, null) as HorizontalScrollView
        val controls = horizontalScrollView.findViewById<LinearLayout>(R.id.controls)

        if (SystemSettings.isPiPSupported(this)) {
            controls.addView(buttonPiP)
        }
        controls.addView(buttonAspectRatio)
        controls.addView(buttonRotation)
        controls.addView(exoSettings)

        exoBasicControls.addView(horizontalScrollView)
        if (Build.VERSION.SDK_INT > 23) {
            horizontalScrollView.setOnScrollChangeListener { view: View?, i: Int, i1: Int, i2: Int, i3: Int -> resetHideCallbacks() }
        }

        viewBinding.videoView.setControllerVisibilityListener {
            TransitionManager.beginDelayedTransition(viewBinding.topBar, Slide(Gravity.TOP))
            viewBinding.topBar.isVisible = it == View.VISIBLE
            if (viewBinding.videoView.restoreControllerTimeout) {
                viewBinding.videoView.restoreControllerTimeout = false
                if (viewModel.player == null || !viewModel.player?.isPlaying!!) {
                    viewBinding.videoView.controllerShowTimeoutMs = -1
                } else {
                    viewBinding.videoView.controllerShowTimeoutMs =
                        CustomStyledPlayerView.CONTROLLER_TIMEOUT
                }
            }

            if (viewBinding.videoView.isControllerFullyVisible) {
                /*if (errorToShow != null) {
                    showError(errorToShow)
                    errorToShow = null
                }*/
            }
        }

        titleViewPadding =
            resources?.getDimensionPixelOffset(R.dimen.exo_styled_bottom_bar_time_padding)!!
        setupTitleView()
        controlView = viewBinding.videoView.findViewById(R.id.exo_controller)
        controlView?.setOnApplyWindowInsetsListener { view: View, windowInsets: WindowInsets? ->
            if (windowInsets != null) {
                view.setPadding(
                    0, windowInsets.systemWindowInsetTop,
                    0, windowInsets.systemWindowInsetBottom
                )
                val insetLeft = windowInsets.systemWindowInsetLeft
                val insetRight = windowInsets.systemWindowInsetRight
                var paddingLeft = 0
                var marginLeft = insetLeft
                var paddingRight = 0
                var marginRight = insetRight
                if (Build.VERSION.SDK_INT >= 28 && windowInsets.displayCutout != null) {
                    if (windowInsets.displayCutout!!.safeInsetLeft == insetLeft) {
                        paddingLeft = insetLeft
                        marginLeft = 0
                    }
                    if (windowInsets.displayCutout!!.safeInsetRight == insetRight) {
                        paddingRight = insetRight
                        marginRight = 0
                    }
                }
                setViewParams(
                    titleView!!,
                    paddingLeft + titleViewPadding,
                    titleViewPadding,
                    paddingRight + titleViewPadding,
                    titleViewPadding,
                    marginLeft,
                    windowInsets.systemWindowInsetTop,
                    marginRight,
                    0
                )
                setViewParams(
                    findViewById(R.id.exo_bottom_bar), paddingLeft, 0, paddingRight, 0,
                    marginLeft, 0, marginRight, 0
                )
                findViewById<View>(R.id.exo_progress).setPadding(
                    windowInsets.systemWindowInsetLeft, 0,
                    windowInsets.systemWindowInsetRight, 0
                )
                setViewMargins(
                    findViewById(R.id.exo_error_message),
                    0,
                    windowInsets.systemWindowInsetTop / 2,
                    0,
                    resources.getDimensionPixelSize(R.dimen.exo_error_message_margin_bottom) + windowInsets.systemWindowInsetBottom / 2
                )
                windowInsets.consumeSystemWindowInsets()
            }
            windowInsets
        }

        timeBar = viewBinding.videoView.findViewById(R.id.exo_progress)

        timeBar?.setBufferedColor(0x33FFFFFF)
        timeBar?.addListener(object : OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                if (viewModel.player == null) {
                    return
                }
                restorePlayState = viewModel.player?.isPlaying!!
                if (restorePlayState) {
                    viewModel.player?.pause()
                }
                scrubbingNoticeable = false
                isScrubbing = true
                frameRendered = true
                viewBinding.videoView.controllerShowTimeoutMs = -1
                scrubbingStart = viewModel.player?.currentPosition!!
                viewModel.player?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                reportScrubbing(position)
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                reportScrubbing(position)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                viewBinding.videoView.setCustomErrorMessage(null)
                isScrubbing = false
                if (restorePlayState) {
                    restorePlayState = false
                    viewBinding.videoView.controllerShowTimeoutMs =
                        CustomStyledPlayerView.CONTROLLER_TIMEOUT
                    viewModel.player?.playWhenReady = true
                }
            }
        })

        viewBinding.btnBack.onClick { onBackPressed() }
        viewBinding.btnShowQueue.onClick {
            VideosQueueFragment.present(supportFragmentManager)
        }
        setupMediaReceiver()
        getVideoInfoFromIntent(intent)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            registerReceiver(
                mReceiver,
                IntentFilter(ACTION_MEDIA_CONTROL)
            )
            if (currentOrientation == Orientation.LANDSCAPE) {
                buttonRotation?.setImageResource(R.drawable.ic_screen_rotation_auto)
                currentOrientation = Orientation.SENSOR
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        } else {
            unregisterReceiver(mReceiver)

            //finish activity when user click on close button in PiP Mode
            if (lifecycle.currentState != Lifecycle.State.STARTED) {
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (getVideoInfoFromIntent(intent))
            viewModel.start()
        else if (!viewModel.player?.isPlaying!!) {
            viewModel.player?.play()
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.playWhenReady) {
            viewModel.start()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.playWhenReady = false
        viewModel.stop()
    }

    override fun onDestroy() {
        viewBinding.videoView.player?.removeListener(playbackStateListener)
        super.onDestroy()
    }

    private fun getVideoInfoFromIntent(intent: Intent?): Boolean {
        var newVideo = false
        val videoId = intent?.getLongExtra(VIDEO_ID, 0)
        if (videoId != null) {
            val videoType = intent.getStringExtra(VIDEO_TYPE)
            val videoName = intent.getStringExtra(VIDEO_NAME)
            titleView?.text = videoName
            val previousId = viewModel.currentId
            viewModel.setCurrentVideo(videoId)
            if (videoId != previousId)
                newVideo = true
        }

        if (intent != null) {
            viewModel.playWhenReady = true
        }

        return newVideo
    }

    @TargetApi(26)
    fun updatePictureInPictureActions(
        iconId: Int,
        resTitle: Int,
        controlType: Int,
        requestCode: Int
    ) {
        val actions = ArrayList<RemoteAction>()
        val intent = PendingIntent.getBroadcast(
            this, requestCode,
            Intent(ACTION_MEDIA_CONTROL).putExtra(
                EXTRA_CONTROL_TYPE,
                controlType
            ), PendingIntent.FLAG_IMMUTABLE
        )
        val icon: Icon = Icon.createWithResource(this@VideoPlayerActivity, iconId)
        val title = getString(resTitle)
        actions.add(RemoteAction(icon, title, title, intent))
        (mPictureInPictureParamsBuilder as PictureInPictureParams.Builder).setActions(actions)
        setPictureInPictureParams((mPictureInPictureParamsBuilder as PictureInPictureParams.Builder).build())
    }

    private fun resetHideCallbacks() {
        if (viewModel.player!!.isPlaying) {
            viewBinding.videoView.controllerShowTimeoutMs =
                CustomStyledPlayerView.CONTROLLER_TIMEOUT
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun enterPiP() {
        val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        if (AppOpsManager.MODE_ALLOWED != appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(),
                packageName
            )
        ) {
            val intent = Intent(
                PIP_SETTINGS, Uri.fromParts(
                    "package",
                    packageName, null
                )
            )
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            return
        }
        viewBinding.videoView.controllerAutoShow = false
        viewBinding.videoView.hideController()
        val format: Format = viewModel.player?.videoFormat!!
        val videoSurfaceView: View = viewBinding.videoView.videoSurfaceView!!
        if (videoSurfaceView is SurfaceView) {
            videoSurfaceView.holder.setFixedSize(format.width, format.height)
        }
        var rational: Rational? = getRational(format)
        if (rational?.toFloat()!! > rationalLimitWide.toFloat()) rational =
            rationalLimitWide else if (rational.toFloat() < rationalLimitTall.toFloat()) rational =
            rationalLimitTall
        (mPictureInPictureParamsBuilder as PictureInPictureParams.Builder).setAspectRatio(rational)
        enterPictureInPictureMode((mPictureInPictureParamsBuilder as PictureInPictureParams.Builder).build())
    }

    private fun getRational(format: Format): Rational {
        return if (isRotated(format)) Rational(
            format.height,
            format.width
        ) else Rational(format.width, format.height)
    }

    private fun isRotated(format: Format): Boolean {
        return format.rotationDegrees == 90 || format.rotationDegrees == 270
    }

    private fun setupPiPButton() {
        if (SystemSettings.isPiPSupported(this)) {
            mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
            updatePictureInPictureActions(
                R.drawable.ic_play_arrow_24dp,
                R.string.exo_controls_play_description,
                CONTROL_TYPE_PLAY,
                REQUEST_PLAY
            )
            buttonPiP = ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom)
            buttonPiP?.setImageResource(R.drawable.ic_picture_in_picture_alt_24dp)
            buttonPiP?.setOnClickListener { enterPiP() }
            buttonPiP?.setOnLongClickListener {
                buttonPiP?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                resetHideCallbacks()
                true
            }
        }
    }

    private fun setupRatioButton() {
        buttonAspectRatio = ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom)
        buttonAspectRatio?.setImageResource(R.drawable.ic_aspect_ratio_24dp)
        buttonAspectRatio?.setOnClickListener { view: View? ->
            viewBinding.videoView.setScale(1f)
            viewBinding.videoView.resizeMode = when (viewBinding.videoView.resizeMode) {
                AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                    viewBinding.videoView.showText(getString(R.string.screen_fixed_w))
                    AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                }
                AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH -> {
                    viewBinding.videoView.showText(getString(R.string.screen_fixed_h))
                    AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                }
                AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT -> {
                    viewBinding.videoView.showText(getString(R.string.screen_fill))
                    AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
                AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                    viewBinding.videoView.showText(getString(R.string.screen_zoom))
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                    viewBinding.videoView.showText(getString(R.string.screen_fit))
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
                else -> {
                    viewBinding.videoView.showText(getString(R.string.screen_fit))
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
            resetHideCallbacks()
        }
    }

    private fun setupRotate() {
        buttonRotation = ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom)
        buttonRotation?.setImageResource(R.drawable.ic_screen_lock_portrait)
        buttonRotation?.setOnClickListener {

            requestedOrientation = when (currentOrientation) {
                Orientation.PORTRAIT -> {
                    buttonRotation?.setImageResource(R.drawable.ic_screen_lock_landscape)
                    currentOrientation = Orientation.LANDSCAPE
                    viewBinding.videoView.showText(getString(R.string.screen_landscape))
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                Orientation.LANDSCAPE -> {
                    buttonRotation?.setImageResource(R.drawable.ic_screen_rotation_auto)
                    currentOrientation = Orientation.SENSOR
                    viewBinding.videoView.showText(getString(R.string.screen_auto))
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }
                Orientation.SENSOR -> {
                    buttonRotation?.setImageResource(R.drawable.ic_screen_lock_portrait)
                    currentOrientation = Orientation.PORTRAIT
                    viewBinding.videoView.showText(getString(R.string.screen_portrait))
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            resetHideCallbacks()
        }
    }

    private fun setupMediaReceiver() {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent == null || ACTION_MEDIA_CONTROL != intent.action || viewModel.player == null) {
                    return
                }
                when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                    CONTROL_TYPE_PLAY -> viewModel.player?.play()
                    CONTROL_TYPE_PAUSE -> viewModel.player?.pause()
                }
            }
        }
    }

    private fun setupTitleView() {

        val centerView: FrameLayout =
            viewBinding.videoView.findViewById(R.id.exo_controls_background)
        titleView = TextView(this)
        titleView?.setBackgroundResource(R.color.ui_controls_background)
        titleView?.setTextColor(Color.WHITE)
        titleView?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        titleView?.setPadding(
            titleViewPadding,
            titleViewPadding,
            titleViewPadding,
            titleViewPadding
        )
        titleView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        titleView?.maxLines = 1
        titleView?.ellipsize = TextUtils.TruncateAt.END
        titleView?.textDirection = View.TEXT_DIRECTION_LOCALE
        centerView.addView(titleView)
    }

    private fun setViewParams(
        view: View,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ) {
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        setViewMargins(
            view,
            marginLeft,
            marginTop,
            marginRight,
            marginBottom
        )
    }

    private fun setViewMargins(
        view: View,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ) {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        view.layoutParams = layoutParams
    }

    fun notifyAudioSessionUpdate(active: Boolean) {
        val intent =
            Intent(if (active) AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION else AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        intent.putExtra(
            AudioEffect.EXTRA_AUDIO_SESSION,
            viewModel.player?.audioSessionId
        )
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
        if (active) {
            intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MOVIE)
        }
        sendBroadcast(intent)
    }

    fun reportScrubbing(position: Long) {
        val diff: Long = position - scrubbingStart
        if (abs(diff) > 1000) {
            scrubbingNoticeable = true
        }
        if (scrubbingNoticeable) {
            viewBinding.videoView.clearIcon()
            viewBinding.videoView.setCustomErrorMessage(viewBinding.videoView.formatMilisSign(diff))
        }
        if (frameRendered) {
            frameRendered = false
            viewModel.player?.seekTo(position)
        }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            viewModel.loudnessEnhancer?.release()
            try {
                viewModel.loudnessEnhancer = LoudnessEnhancer(audioSessionId)
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            notifyAudioSessionUpdate(true)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            viewBinding.videoView.keepScreenOn = isPlaying

            if (SystemSettings.isPiPSupported(this@VideoPlayerActivity)) {
                if (isPlaying) {
                    updatePictureInPictureActions(
                        R.drawable.ic_pause_24dp,
                        R.string.exo_controls_pause_description,
                        CONTROL_TYPE_PAUSE,
                        REQUEST_PAUSE
                    )
                } else {
                    updatePictureInPictureActions(
                        R.drawable.ic_play_arrow_24dp,
                        R.string.exo_controls_play_description,
                        CONTROL_TYPE_PLAY,
                        REQUEST_PLAY
                    )
                }
            }

            if (!isScrubbing) {
                if (isPlaying) {
                    if (viewBinding.videoView.shortControllerTimeout) {
                        viewBinding.videoView.controllerShowTimeoutMs =
                            CustomStyledPlayerView.CONTROLLER_TIMEOUT / 3
                        viewBinding.videoView.shortControllerTimeout = false
                        viewBinding.videoView.restoreControllerTimeout = true
                    } else {
                        viewBinding.videoView.controllerShowTimeoutMs =
                            CustomStyledPlayerView.CONTROLLER_TIMEOUT
                    }
                } else {
                    viewBinding.videoView.controllerShowTimeoutMs = -1
                }
            }

            if (!isPlaying) {
                viewBinding.videoView.locked = false
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_READY) {
                frameRendered = true
            }
        }
    }

    companion object {
        const val TAG = "VideoPlayerActivity1"
        const val ACTION_MEDIA_CONTROL = "media_control"
        const val EXTRA_CONTROL_TYPE = "control_type"
        const val VIDEO_ID = "video_id"
        const val VIDEO_NAME = "video_name"
        const val VIDEO_TYPE = "video_type"
        const val PIP_SETTINGS = "android.settings.PICTURE_IN_PICTURE_SETTINGS"

        const val REQUEST_PLAY = 1
        const val REQUEST_PAUSE = 2
        const val CONTROL_TYPE_PLAY = 1
        const val CONTROL_TYPE_PAUSE = 2
    }
}

enum class Orientation {
    PORTRAIT, LANDSCAPE, SENSOR
}

