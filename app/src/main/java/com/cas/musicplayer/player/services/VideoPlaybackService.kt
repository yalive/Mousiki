package com.cas.musicplayer.player.services

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.cas.common.event.EventObserver
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.musicplayer.player.*
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */
class VideoPlaybackService : LifecycleService() {

    val TAG = "VideoPlaybackService"

    companion object {
        val COMMAND_PLAY_TRACK = "video-id"
        val COMMAND_RESUME = "resume"
        val COMMAND_PAUSE = "pause"
        val COMMAND_SEEK_TO = "seek-to"
        val COMMAND_SEEK_TO_FROM_FULL_SCREEN = "seek-to-from-fullscreen"
        val COMMAND_HIDE_VIDEO = "hide-video"
        val COMMAND_SHOW_VIDEO = "show-video"

        val CLICK_ACTION_THRESHOLD = 160
    }

    lateinit var windowManager: WindowManager
    lateinit var videoContainerView: View
    lateinit var bottomView: View
    lateinit var youTubePlayerView: YouTubePlayerView
    var draggableView: View? = null

    lateinit var videoViewParams: WindowManager.LayoutParams

    var videoId: String? = null

    var youTubePlayer: YouTubePlayer? = null

    var videoEmplacement: VideoEmplacement = VideoEmplacement.bottom()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.getBooleanExtra(COMMAND_RESUME, false)?.let { resume ->
            if (resume) {
                youTubePlayer?.play()
            }
        }

        intent?.getBooleanExtra(COMMAND_PAUSE, false)?.let { pause ->
            if (pause) {
                youTubePlayer?.pause()
            }
        }

        intent?.getStringExtra(COMMAND_PLAY_TRACK)?.let {
            PlayerQueue.value?.let { currentTrack ->
                youTubePlayer?.loadVideo(currentTrack.youtubeId, 0f)
            }
        }

        intent?.getLongExtra(COMMAND_SEEK_TO, -1)?.let { seekTo ->
            if (seekTo.toInt() != -1) {
                val fromFullScreen = intent.getBooleanExtra(COMMAND_SEEK_TO_FROM_FULL_SCREEN, false)
                PlayerQueue.value?.let { currentTrack ->

                    if (fromFullScreen) {
                        youTubePlayer?.loadVideo(currentTrack.youtubeId, seekTo.toFloat())

                    } else {
                        youTubePlayer?.seekTo(seekTo.toFloat())
                    }
                }
            }
        }

        intent?.getBooleanExtra(COMMAND_HIDE_VIDEO, false)?.let { hideVideo ->
            if (hideVideo) {
                videoViewParams.width = 0
                videoViewParams.height = 0
                windowManager.updateViewLayout(videoContainerView, videoViewParams)

            }
        }

        intent?.getBooleanExtra(COMMAND_SHOW_VIDEO, false)?.let { showVideo ->
            if (showVideo) {
                videoContainerView.visible()
                videoViewParams.width = videoEmplacement.width
                videoViewParams.height = videoEmplacement.height
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        addBottomView()

        if (Constants.Config.DEBUG_PLAYER) {
            addDebugView()
        }

        preparePlayerView()

        observeForegroundToggle()

        observeBottomPanelDragging()

        observeSlidePanelDragging()

        observeBottomSheetPlayerDragging()

        observeAdsVisibility()

        startForegroundService()
    }


    private fun preparePlayerView() {
        videoContainerView =
            LayoutInflater.from(this)
                .inflate(com.cas.musicplayer.R.layout.view_floating_video, null)

        youTubePlayerView =
            videoContainerView.findViewById(com.cas.musicplayer.R.id.youtubePlayerView)
        lifecycle.addObserver(youTubePlayerView)


        //Add the view to the window.
        val type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> WindowManager.LayoutParams.TYPE_PHONE
        }


        videoViewParams = WindowManager.LayoutParams(
            videoEmplacement.width,
            videoEmplacement.height,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        videoViewParams.gravity = Gravity.TOP or Gravity.START
        videoViewParams.x = videoEmplacement.x
        videoViewParams.y = videoEmplacement.y


        //Drag and move chat head using user's touch action.
        draggableView =
            videoContainerView.findViewById<View>(com.cas.musicplayer.R.id.draggableView)
        draggableView?.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                if (videoEmplacement !is EmplacementOut) {
                    return true
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        bottomView.visibility = View.VISIBLE

                        //remember the initial position.
                        initialX = videoViewParams.x
                        initialY = videoViewParams.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        bottomView.visibility = View.GONE

                        val endX = event.rawX
                        val endY = event.rawY

                        if (isAClick(initialTouchX, endX, initialTouchY, endY)) {
                            //Open app
                            val intent = Intent(this@VideoPlaybackService, MainActivity::class.java)
                            intent.putExtra(MainActivity.EXTRAS_FROM_PLAY_SERVICE, true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (bottomView.isActivated) {
                            stopSelf()
                        }

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

                        //Calculate the X and Y coordinates of the view.
                        videoViewParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        videoViewParams.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(videoContainerView, videoViewParams)
                        lastAction = event.action


                        val y = videoViewParams.y

                        bottomView.isActivated =
                            screenSize().heightPx - y - youTubePlayerView.height - bottomView.height <= 0

                        return true
                    }
                }
                return false
            }
        })


        windowManager.addView(videoContainerView, videoViewParams)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                PlayerQueue.value?.let { currentTrack ->
                    youTubePlayer.loadVideo(currentTrack.youtubeId, 0f)
                }
                this@VideoPlaybackService.youTubePlayer = youTubePlayer

            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                PlaybackLiveData.value = state
                if (state == PlayerConstants.PlayerState.ENDED) {
                    PlayerQueue.playNextTrack()
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                if (isScreenLocked()) {
                    PlayerQueue.pause()
                    return
                }
                super.onCurrentSecond(youTubePlayer, second)
                PlaybackDuration.value = second
            }
        })
    }

    private val fullScreenListener = object : YouTubePlayerFullScreenListener {
        override fun onYouTubePlayerEnterFullScreen() {
            Log.d(TAG, "onYouTubePlayerEnterFullScreen")
        }

        override fun onYouTubePlayerExitFullScreen() {
            Log.d(TAG, "onYouTubePlayerExitFullScreen")
            youTubePlayerView.exitFullScreen()
            VideoEmplacementLiveData.center()

        }
    }


    private fun observeForegroundToggle() {

        VideoEmplacementLiveData.observe(this, Observer { emplacement ->
            this.videoEmplacement = emplacement

            if (emplacement is EmplacementFullScreen) {
                toggleFullScreenVideoPlayer(true)

            } else {
                toggleFullScreenVideoPlayer(false)
            }

            if (emplacement is EmplacementOut) {
                videoViewParams.flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            } else {
                videoViewParams.flags =
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            }

            videoViewParams.x = emplacement.x
            videoViewParams.y = emplacement.y

            videoViewParams.width = emplacement.width
            videoViewParams.height = emplacement.height
            //Update the layout with new X & Y coordinate
            windowManager.updateViewLayout(videoContainerView, videoViewParams)

            videoContainerView.alpha = 1f
        })
    }

    private fun toggleFullScreenVideoPlayer(fullScreen: Boolean) {
        youTubePlayerView.getPlayerUiController().showPlayPauseButton(fullScreen)
        youTubePlayerView.getPlayerUiController().showCurrentTime(fullScreen)
        youTubePlayerView.getPlayerUiController().showDuration(fullScreen)
        youTubePlayerView.getPlayerUiController().showSeekBar(fullScreen)
        youTubePlayerView.getPlayerUiController().showUi(fullScreen)
        youTubePlayerView.getPlayerUiController().showBufferingProgress(fullScreen)
        youTubePlayerView.getPlayerUiController().showVideoTitle(fullScreen)

        if (fullScreen) {
            youTubePlayerView.enterFullScreen()
            draggableView?.gone()
        } else {
            if (youTubePlayerView.isFullScreen()) {
                youTubePlayerView.exitFullScreen()
            }
            draggableView?.visible()
        }
    }


    private fun observeBottomPanelDragging() {

        DragBottomPanelLiveData.observe(this, Observer { dragPanelInfo ->
            if (videoEmplacement is EmplacementCenter) {
                videoViewParams.y = dragPanelInfo.pannelY.toInt() + videoEmplacement.y
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            } else if (videoEmplacement is EmplacementBottom) {
                videoViewParams.y =
                    dragPanelInfo.pannelY.toInt() - dpToPixel(18f) // -minus is workaround: To be fixed todo
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
                videoContainerView.alpha = 1 - dragPanelInfo.slideOffset
            }
        })
    }

    val bottomEmp = EmplacementBottom()
    val centerEmp = EmplacementCenter()

    private fun observeSlidePanelDragging() {

        DragSlidePanelMonitor.observe(this, Observer { progress ->

            if (videoEmplacement is EmplacementCenter) {
                videoViewParams.x = (centerEmp.x + progress * dpToPixel(280f)).toInt()
                videoViewParams.y = (centerEmp.y + progress * dpToPixel(20f)).toInt()
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            } else if (videoEmplacement is EmplacementBottom) {

                videoViewParams.x = (bottomEmp.x + progress * dpToPixel(280f)).toInt()
                videoViewParams.y = (bottomEmp.y - progress * dpToPixel(56f)).toInt()
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            }

        })
    }


    private fun observeBottomSheetPlayerDragging() {

        DragBottomSheetMonitor.observe(this, Observer { progress ->

            if (videoEmplacement is EmplacementPlaylist) {
                videoViewParams.y = progress
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            }

        })
    }

    val handler = Handler()

    private fun observeAdsVisibility() {
        OnShowAdsListener.observe(this, EventObserver { shown ->
            if (shown) {
                videoContainerView.gone()
                handler.postDelayed({
                    youTubePlayer?.pause()
                }, 1000)
            } else {
                videoContainerView.visible()
                handler.postDelayed({
                    youTubePlayer?.play()
                }, 1000)
            }
        })
    }

    private fun addBottomView() {

        bottomView =
            LayoutInflater.from(this)
                .inflate(com.cas.musicplayer.R.layout.bottom_floating_player, null)
        bottomView.visibility = View.GONE

        val type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> WindowManager.LayoutParams.TYPE_PHONE
        }

        val bottomViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPixel(60f),
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the chat head position
        //Initially view will be added to top-left corner
        bottomViewParams.gravity = Gravity.BOTTOM or Gravity.START
        bottomViewParams.x = 0
        bottomViewParams.y = 0


        windowManager.addView(bottomView, bottomViewParams)
    }

    var debugView: View? = null

    private fun addDebugView() {

        debugView =
            LayoutInflater.from(this)
                .inflate(com.cas.musicplayer.R.layout.view_debug_floating_player, null)

        val debugTextView = debugView?.findViewById<TextView>(com.cas.musicplayer.R.id.txtDebug)
        val type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> WindowManager.LayoutParams.TYPE_PHONE
        }

        val debugViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPixel(60f),
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the chat head position
        //Initially view will be added to top-left corner
        debugViewParams.gravity = Gravity.TOP or Gravity.START
        debugViewParams.x = 0
        debugViewParams.y = 0


        windowManager.addView(debugView, debugViewParams)

        PlaybackLiveData.observe(this, Observer {
            debugTextView?.text = it.name
        })
    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = Math.abs(startX - endX)
        val differenceY = Math.abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD)
    }

    override fun onDestroy() {
        super.onDestroy()
        PlaybackLiveData.value = PlayerConstants.PlayerState.UNKNOWN
        windowManager.removeView(videoContainerView)
        windowManager.removeView(bottomView)
        if (debugView != null) {
            windowManager.removeView(debugView)
        }
    }


    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        val helper = NotificationHelper(this)
        helper.init()

    }
}