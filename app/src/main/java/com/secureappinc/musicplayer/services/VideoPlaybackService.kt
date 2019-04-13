package com.secureappinc.musicplayer.services

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.EmplacementBottom
import com.secureappinc.musicplayer.models.EmplacementCenter
import com.secureappinc.musicplayer.models.EmplacementOut
import com.secureappinc.musicplayer.models.VideoEmplacement
import com.secureappinc.musicplayer.player.PlayerQueue
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.utils.VideoEmplacementLiveData
import com.secureappinc.musicplayer.utils.dpToPixel
import com.secureappinc.musicplayer.utils.screenSize


/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */
class VideoPlaybackService : LifecycleService() {

    companion object {
        val COMMAND_PLAY_TRACK = "video-id"
        val COMMAND_RESUME = "resume"
        val COMMAND_PAUSE = "pause"
        val COMMAND_SEEK_TO = "seek-to"
    }

    lateinit var windowManager: WindowManager
    lateinit var videoContainerView: View
    lateinit var bottomView: View
    lateinit var youTubePlayerView: YouTubePlayerView

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

        intent?.getStringExtra(COMMAND_PLAY_TRACK)?.let { trackId ->
            videoId = trackId
            youTubePlayer?.loadVideo(trackId, 0f)
        }

        intent?.getLongExtra(COMMAND_SEEK_TO, -1)?.let { seekTo ->
            if (seekTo.toInt() != -1 && videoId != null) {
                youTubePlayer?.seekTo(seekTo.toFloat())
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        addBottomView()

        preparePlayerView()

        //observeClickControls()

        observeForegroundToggle()

        observeBottomPanelDragging()
    }


    private fun preparePlayerView() {
        videoContainerView = LayoutInflater.from(this).inflate(R.layout.view_floating_video, null)

        youTubePlayerView = videoContainerView.findViewById(R.id.youtubePlayerView)
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
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        videoViewParams.gravity = Gravity.TOP or Gravity.START
        videoViewParams.x = videoEmplacement.x
        videoViewParams.y = videoEmplacement.y


        //Drag and move chat head using user's touch action.
        videoContainerView.findViewById<View>(R.id.draggableView).setOnTouchListener(object : View.OnTouchListener {
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
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
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
                videoId?.let { id ->
                    youTubePlayer.loadVideo(id, 0f)
                }
                this@VideoPlaybackService.youTubePlayer = youTubePlayer

            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)
                PlaybackLiveData.value = state
                if (state == PlayerConstants.PlayerState.ENDED) {
                    PlayerQueue.playNextTrack()
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                PlaybackDuration.value = second
            }

        })
    }


    private fun observeForegroundToggle() {

        VideoEmplacementLiveData.observe(this, Observer { emplacement ->
            this.videoEmplacement = emplacement

            if (emplacement is EmplacementOut) {
                videoViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            } else {
                videoViewParams.flags =
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
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


    private fun addBottomView() {

        bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_floating_player, null)
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


    override fun onDestroy() {
        super.onDestroy()
        PlaybackLiveData.value = PlayerConstants.PlayerState.UNKNOWN
        windowManager.removeView(videoContainerView)
        windowManager.removeView(bottomView)
    }
}