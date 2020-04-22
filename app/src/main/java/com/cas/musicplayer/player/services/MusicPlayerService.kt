package com.cas.musicplayer.player.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.cas.common.event.EventObserver
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.*
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.loadBitmap
import com.cas.musicplayer.utils.screenSize
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch


/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */
class MusicPlayerService : LifecycleService(), SleepTimer by MusicSleepTimer() {
    private lateinit var windowManager: WindowManager
    private lateinit var videoContainerView: View
    private lateinit var bottomView: View
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var favouriteReceiver: FavouriteReceiver
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat
    private var draggableView: View? = null
    private lateinit var videoViewParams: WindowManager.LayoutParams
    private var videoEmplacement: VideoEmplacement = VideoEmplacement.bottom(true)
    private val handler = Handler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
        }
        intent?.getBooleanExtra(COMMAND_RESUME, false)?.let { resume ->
            if (resume) {
                mediaController.transportControls.play()
            }
        }

        intent?.getBooleanExtra(COMMAND_PAUSE, false)?.let { pause ->
            if (pause) {
                mediaController.transportControls.pause()
            }
        }

        intent?.getStringExtra(COMMAND_PLAY_TRACK)?.let {
            PlayerQueue.value?.let { currentTrack ->
                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    currentTrack.title
                )
                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                    currentTrack.title
                )
                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    currentTrack.imgUrl
                )
                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_ART_URI,
                    currentTrack.imgUrl
                )
                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    currentTrack.imgUrl
                )

                metadataBuilder.putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                    currentTrack.youtubeId
                )

                mediaSession.setMetadata(metadataBuilder.build())
                mediaController.transportControls.playFromMediaId(
                    currentTrack.youtubeId,
                    null
                )
                lifecycleScope.launch {
                    val loadBitmap = Picasso.get().loadBitmap(currentTrack.imgUrl)
                    metadataBuilder.putBitmap(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        loadBitmap
                    )
                    mediaSession.setMetadata(metadataBuilder.build())
                }
            }
        }

        intent?.getLongExtra(COMMAND_SEEK_TO, -1)?.let { seekTo ->
            if (seekTo.toInt() != -1) {
                PlayerQueue.value?.let { currentTrack ->
                    mediaController.transportControls.seekTo(seekTo)
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

        intent?.getIntExtra(COMMAND_SCHEDULE_TIMER, -1)?.let { duration ->
            if (duration > 0) {
                stopPlayer(afterDuration = duration)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private lateinit var youtubePlayerManager: YoutubePlayerManager

    override fun onCreate() {
        super.onCreate()
        val mediaSessionCallback = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                youtubePlayerManager.play()
            }

            override fun onPause() {
                youtubePlayerManager.pause()
            }

            override fun onSeekTo(pos: Long) {
                youtubePlayerManager.seekTo(pos.toFloat() / 1000)
            }

            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                mediaId?.let {
                    youtubePlayerManager.loadVideo(mediaId, 0f)
                }
            }

            override fun onSkipToNext() {
                PlayerQueue.playNextTrack()
            }

            override fun onSkipToPrevious() {
                PlayerQueue.playPreviousTrack()
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                val component = MusicApp.get().component
                if (action == CustomAction.ADD_CURRENT_MEDIA_TO_FAVOURITE) {
                    lifecycleScope.launch {
                        val metadata = mediaSession.controller.metadata
                        val mediaId = metadata.description.mediaId
                        val title = metadata.description.title
                        val duration = PlayerQueue.value?.duration
                        if (mediaId != null && title != null && duration != null) {
                            val track = MusicTrack(
                                youtubeId = mediaId,
                                title = title.toString(),
                                duration = duration
                            )
                            component.addSongToFavourite(track)
                        }
                    }
                } else if (action == CustomAction.REMOVE_CURRENT_MEDIA_FROM_FAVOURITE) {
                    lifecycleScope.launch {
                        val metadata = mediaSession.controller.metadata
                        val mediaId = metadata.description.mediaId
                        mediaId?.let {
                            component.removeSongFromFavouriteList(mediaId)
                        }
                    }
                }
                val playbackState = mediaSession.controller.playbackState
                playbackState?.let {
                    mediaSession.setPlaybackState(playbackState)
                }
            }
        }

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(applicationContext, "Mousiki").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            isActive = true
            setCallback(mediaSessionCallback)
            setSessionActivity(sessionActivityPendingIntent)
        }
        mediaController = MediaControllerCompat(this, mediaSession).apply {
            registerCallback(MediaControllerCallback())
        }
        youtubePlayerManager = YoutubePlayerManager(mediaController, mediaSession)
        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)
        favouriteReceiver = FavouriteReceiver(this, mediaSession.sessionToken)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        addBottomView()

        preparePlayerView()

        observeForegroundToggle()

        observeBottomPanelDragging()

        observeSlidePanelDragging()

        observeBottomSheetPlayerDragging()

        observeAdsVisibility()

        becomingNoisyReceiver.register()
        favouriteReceiver.register()
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
                            val intent = Intent(this@MusicPlayerService, MainActivity::class.java)
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
        youTubePlayerView.addYouTubePlayerListener(youtubePlayerManager)
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

    val centerEmp = EmplacementCenter()

    private fun observeSlidePanelDragging() {
        DragSlidePanelMonitor.observe(this, Observer { progress ->
            if (videoEmplacement is EmplacementCenter) {
                videoViewParams.x = (centerEmp.x + progress * dpToPixel(280f)).toInt()
                videoViewParams.y = (centerEmp.y + progress * dpToPixel(20f)).toInt()
                windowManager.updateViewLayout(videoContainerView, videoViewParams)
            } else if (videoEmplacement is EmplacementBottom) {
                val bottomEmp = videoEmplacement
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

    private fun observeAdsVisibility() {
        OnShowAdsListener.observe(this, EventObserver { shown ->
            if (shown) {
                videoContainerView.gone()
                handler.postDelayed({
                    mediaController.transportControls.pause()
                }, 1000)
            } else {
                videoContainerView.visible()
                handler.postDelayed({
                    mediaController.transportControls.play()
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

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = Math.abs(startX - endX)
        val differenceY = Math.abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        becomingNoisyReceiver.unregister()
        favouriteReceiver.unregister()
        PlaybackLiveData.value = PlayerConstants.PlayerState.UNKNOWN
        windowManager.removeView(videoContainerView)
        windowManager.removeView(bottomView)
    }


    /**
     * Class to receive callbacks about state changes to the [MediaSessionCompat]. In response
     * to those callbacks, this class:
     *
     * - Build/update the service's notification.
     * - Register/unregister a broadcast receiver for [AudioManager.ACTION_AUDIO_BECOMING_NOISY].
     * - Calls [Service.startForeground] and [Service.stopForeground].
     */
    private var isForegroundService = false

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let { state ->
                lifecycleScope.launch {
                    updateNotification(state)
                }
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { statePlayer ->
                lifecycleScope.launch {
                    updateNotification(statePlayer)
                }
            }
        }

        private suspend fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state
            // Skip building a notification when state is "none" and metadata is null.
            val notification = if (mediaController.metadata != null
                && updatedState != PlaybackStateCompat.STATE_NONE
            ) {
                notificationBuilder.buildNotification(mediaSession.sessionToken)
            } else null

            when (updatedState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    becomingNoisyReceiver.register()
                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        if (!isForegroundService) {
                            startForeground(NOW_PLAYING_NOTIFICATION, notification)
                            isForegroundService = true
                        }
                    }
                }
                else -> {
                    becomingNoisyReceiver.unregister()
                    if (isForegroundService) {
                        isForegroundService = false
                        if (notification != null) {
                            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = "VideoPlaybackService"
        val COMMAND_PLAY_TRACK = "video-id"
        val COMMAND_RESUME = "resume"
        val COMMAND_PAUSE = "pause"
        val COMMAND_SEEK_TO = "seek-to"
        val COMMAND_HIDE_VIDEO = "hide-video"
        val COMMAND_SHOW_VIDEO = "show-video"
        val COMMAND_SCHEDULE_TIMER = "schedule-timer"

        val CLICK_ACTION_THRESHOLD = 160

        object CustomAction {
            const val ADD_CURRENT_MEDIA_TO_FAVOURITE = "add_current_media_to_Favourite"
            const val REMOVE_CURRENT_MEDIA_FROM_FAVOURITE = "remove_current_media_from_Favourite"
        }
    }
}


