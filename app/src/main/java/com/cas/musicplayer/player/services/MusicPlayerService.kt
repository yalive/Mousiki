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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.cas.common.event.EventObserver
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.di.AppComponent
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.OnShowAdsListener
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.YoutubeFloatingPlayerView
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.loadBitmap
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */
class MusicPlayerService : LifecycleService(), SleepTimer by MusicSleepTimer() {
    private lateinit var windowManager: WindowManager
    private lateinit var bottomView: View
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var favouriteReceiver: FavouriteReceiver
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var floatingPlayerView: YoutubeFloatingPlayerView
    private val handler = Handler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras?.getString(Intent.EXTRA_PACKAGE_NAME) == "android") {
            handleLastSessionSysMediaButton()
        }
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
                floatingPlayerView.hide()
            }
        }

        intent?.getBooleanExtra(COMMAND_SHOW_VIDEO, false)?.let { showVideo ->
            if (showVideo) {
                floatingPlayerView.show()
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
                            injector.addSongToFavourite(track)
                        }
                    }
                } else if (action == CustomAction.REMOVE_CURRENT_MEDIA_FROM_FAVOURITE) {
                    lifecycleScope.launch {
                        val metadata = mediaSession.controller.metadata
                        val mediaId = metadata.description.mediaId
                        mediaId?.let {
                            injector.removeSongFromFavouriteList(mediaId)
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

        floatingPlayerView = YoutubeFloatingPlayerView(this)
        floatingPlayerView.preparePlayerView(
            service = this,
            youtubePlayerManager = youtubePlayerManager,
            bottomView = bottomView
        )
        observeForegroundToggle()

        observeBottomPanelDragging()

        observeAdsVisibility()

        favouriteReceiver.register()
    }

    private fun observeForegroundToggle() {
        VideoEmplacementLiveData.observe(this, Observer { emplacement ->
            floatingPlayerView.onVideoEmplacementChanged(emplacement)
        })
    }

    private fun observeBottomPanelDragging() {
        DragBottomPanelLiveData.observe(this, Observer { dragPanelInfo ->
            floatingPlayerView.onDragBottomPanel(dragPanelInfo)
        })
    }

    private fun observeAdsVisibility() {
        OnShowAdsListener.observe(this, EventObserver { shown ->
            floatingPlayerView.onAdsVisible(shown)
            if (shown) {
                handler.postDelayed({
                    mediaController.transportControls.pause()
                }, 1000)
            } else {
                handler.postDelayed({
                    mediaController.transportControls.play()
                }, 1000)
            }
        })
    }

    private fun addBottomView() {
        bottomView = LayoutInflater.from(this)
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

        bottomViewParams.gravity = Gravity.BOTTOM or Gravity.START
        bottomViewParams.x = 0
        bottomViewParams.y = 0

        windowManager.addView(bottomView, bottomViewParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        becomingNoisyReceiver.unregister()
        favouriteReceiver.unregister()
        PlaybackLiveData.value = PlayerConstants.PlayerState.UNKNOWN
        floatingPlayerView.removeFromWindow()
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
                        stopForeground(false)
                    }
                }
            }
        }
    }

    private fun handleLastSessionSysMediaButton() {
        lifecycleScope.launch {
            injector.analytics.logEvent(START_PLAYER_FORM_LAST_SESSION, null)
            val recentlyPlayedSongs = injector.getRecentlyPlayedSongs
            val recentTracks = recentlyPlayedSongs()
            if (recentTracks.isNotEmpty()) {
                PlayerQueue.playTrack(recentTracks[0], recentTracks)
                delay(500)
                VideoEmplacementLiveData.out()
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

        object CustomAction {
            const val ADD_CURRENT_MEDIA_TO_FAVOURITE = "add_current_media_to_Favourite"
            const val REMOVE_CURRENT_MEDIA_FROM_FAVOURITE = "remove_current_media_from_Favourite"
        }
    }
}

private val MusicPlayerService.injector: AppComponent
    get() = (application as MusicApp).component


// Analytics
private const val START_PLAYER_FORM_LAST_SESSION = "player_started_by_last_session"


