package com.cas.musicplayer.player.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.cas.common.extensions.bool
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.YoutubeFloatingPlayerView
import com.cas.musicplayer.player.extensions.albumArt
import com.cas.musicplayer.player.extensions.isPlaying
import com.cas.musicplayer.player.extensions.musicTrack
import com.cas.musicplayer.player.receiver.BecomingNoisyReceiver
import com.cas.musicplayer.player.receiver.DeleteNotificationReceiver
import com.cas.musicplayer.player.receiver.FavouriteReceiver
import com.cas.musicplayer.player.receiver.LockScreenReceiver
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */
class MusicPlayerService : LifecycleService(), SleepTimer by MusicSleepTimer() {

    lateinit var mediaSession: MediaSessionCompat
        private set

    private val windowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val notificationBuilder by lazy { NotificationBuilder(this) }
    private val youtubePlayerManager by lazy { YoutubePlayerManager(mediaController, mediaSession) }
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val binder = ServiceBinder()
    private val metadataBuilder = MediaMetadataCompat.Builder()

    private lateinit var mediaController: MediaControllerCompat

    // Receivers
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var deleteNotificationReceiver: DeleteNotificationReceiver
    private lateinit var lockScreenReceiver: LockScreenReceiver
    private lateinit var favouriteReceiver: FavouriteReceiver

    // Over apps views
    private lateinit var bottomView: View
    private lateinit var batterySaverView: View
    private lateinit var floatingPlayerView: YoutubeFloatingPlayerView

    override fun onCreate() {
        super.onCreate()

        // Prepare media session
        setUpMediaSession()

        // Register receivers
        prepareReceivers()

        // Draw view to window manager
        addBottomView()
        createBatterySaverView()
        createFloatingPlayerView()

        // React to player position
        observeForegroundToggle()

        // Move service to foreground
        moveToForeground()
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Move service to foreground
        moveToForeground()

        // check last media session
        if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            val event = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (event?.action == KeyEvent.ACTION_DOWN && intent.hasExtra(Intent.EXTRA_PACKAGE_NAME)) {
                handleLastSessionSysMediaButton()
            } else {
                MediaButtonReceiver.handleIntent(mediaSession, intent)
            }
        }

        // Extract command
        when (val command = extractCommand(intent)) {
            PlayerCommand.PlayTrack -> playCurrentTrack()
            PlayerCommand.Resume -> resumePlayback()
            PlayerCommand.Pause -> mediaController.transportControls.pause()
            is PlayerCommand.SeekTo -> mediaController.transportControls.seekTo(command.seconds)
            is PlayerCommand.ScheduleTimer -> stopPlayer(command.duration)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteNotificationReceiver.unregister()
        lockScreenReceiver.unregister()
        mediaSession.release()
        becomingNoisyReceiver.unregister()
        favouriteReceiver.unregister()
        PlaybackLiveData.value = PlayerConstants.PlayerState.UNKNOWN
        floatingPlayerView.removeFromWindow()
        try {
            windowManager.removeView(bottomView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setUpMediaSession() {
        val mediaSessionCallback = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                if (isScreenLocked()) return
                youtubePlayerManager.play()
            }

            override fun onPause() {
                youtubePlayerManager.pause()
            }

            override fun onStop() {
                stopForeground(true)
            }

            override fun onSeekTo(pos: Long) {
                if (isScreenLocked()) return
                youtubePlayerManager.seekTo(pos.toFloat() / 1000)
            }

            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                if (isScreenLocked()) return
                mediaId?.let {
                    youtubePlayerManager.loadVideo(mediaId, 0f)
                }
            }

            override fun onSkipToNext() {
                if (isScreenLocked()) return
                PlayerQueue.playNextTrack()
            }

            override fun onSkipToPrevious() {
                if (isScreenLocked()) return
                PlayerQueue.playPreviousTrack()
            }

            override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
                if (command == CustomCommand.ENABLE_NOTIFICATION_ACTIONS) {
                    youtubePlayerManager.onScreenUnlocked()
                } else if (command == CustomCommand.DISABLE_NOTIFICATION_ACTIONS) {
                    youtubePlayerManager.onScreenLocked()
                }
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                val metadata: MediaMetadataCompat = mediaSession.controller.metadata ?: return
                lifecycleScope.launch {
                    if (action == CustomAction.ADD_TO_FAVOURITE) {
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
                    } else if (action == CustomAction.REMOVE_FROM_FAVOURITE) {
                        val mediaId = metadata.description.mediaId
                        mediaId?.let {
                            injector.removeSongFromFavouriteList(mediaId)
                        }
                    }
                    val playbackState = mediaSession.controller.playbackState
                    playbackState?.let {
                        mediaSession.setPlaybackState(playbackState)
                    }
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
    }

    private fun prepareReceivers() {
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)
        deleteNotificationReceiver = DeleteNotificationReceiver(
            this, mediaSession.sessionToken
        ).apply { register() }

        lockScreenReceiver = LockScreenReceiver(
            this, mediaSession.sessionToken
        ).apply { register() }

        favouriteReceiver = FavouriteReceiver(
            this, mediaSession.sessionToken
        ).apply { register() }
    }

    private fun moveToForeground() = lifecycleScope.launch {
        val notification = notificationBuilder.buildNotification(mediaSession.sessionToken)
        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
        startForeground(NOW_PLAYING_NOTIFICATION, notification)
        scheduleStopForeground()
    }

    private fun playCurrentTrack() {
        val currentTrack = PlayerQueue.value ?: return
        metadataBuilder.musicTrack = currentTrack
        mediaSession.setMetadata(metadataBuilder.build())
        mediaController.transportControls.playFromMediaId(currentTrack.youtubeId, null)

        lifecycleScope.launch(Dispatchers.IO) {
            injector.addTrackToRecentlyPlayed(currentTrack)
            val loadBitmap = Picasso.get().getBitmap(currentTrack.imgUrl, 320)
            metadataBuilder.albumArt = loadBitmap
            mediaSession.setMetadata(metadataBuilder.build())
        }
    }

    private fun resumePlayback() {
        val currentTrack = PlayerQueue.value ?: return
        metadataBuilder.musicTrack = currentTrack
        mediaSession.setMetadata(metadataBuilder.build())
        mediaController.transportControls.play()
    }

    private fun extractCommand(intent: Intent?): PlayerCommand? {
        if (intent == null) return null
        if (intent.bool(COMMAND_RESUME)) return PlayerCommand.Resume
        if (intent.bool(COMMAND_PAUSE)) return PlayerCommand.Pause
        if (intent.bool(COMMAND_PLAY_TRACK)) return PlayerCommand.PlayTrack
        val seekTo = intent.getLongExtra(COMMAND_SEEK_TO, -1)
        if (seekTo > 0) {
            return PlayerCommand.SeekTo(seekTo)
        }

        val scheduleTimer = intent.getIntExtra(COMMAND_SCHEDULE_TIMER, -1)
        if (scheduleTimer > 0) {
            return PlayerCommand.ScheduleTimer(scheduleTimer)
        }
        return null
    }

    private fun observeForegroundToggle() {
        VideoEmplacementLiveData.observe(this, Observer { emplacement ->
            floatingPlayerView.onVideoEmplacementChanged(emplacement)
        })
    }

    private fun addBottomView() {
        bottomView = LayoutInflater.from(this)
            .inflate(R.layout.bottom_floating_player, null)
            .apply { isVisible = false }

        val bottomViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPixel(56f),
            windowOverlayTypeOrPhone,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        bottomViewParams.gravity = Gravity.BOTTOM or Gravity.START
        bottomViewParams.x = 0
        bottomViewParams.y = 0

        if (canDrawOverApps()) {
            windowManager.addView(bottomView, bottomViewParams)
        }
    }

    private fun createBatterySaverView() {
        batterySaverView = LayoutInflater.from(this)
            .inflate(R.layout.battery_saver_floating_view, null)
            .apply { isVisible = false }
        val batterySaverViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPixel(56f),
            windowOverlayTypeOrPhone,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        batterySaverViewParams.gravity = Gravity.TOP or Gravity.START
        batterySaverViewParams.x = 0
        batterySaverViewParams.y = 0

        if (canDrawOverApps()) {
            windowManager.addView(batterySaverView, batterySaverViewParams)
        }
    }

    private fun createFloatingPlayerView() {
        floatingPlayerView = YoutubeFloatingPlayerView(
            ContextThemeWrapper(this, R.style.Theme_MaterialComponents)
        )

        floatingPlayerView.preparePlayerView(
            service = this,
            youtubePlayerManager = youtubePlayerManager,
            bottomView = bottomView,
            batterySaverView = batterySaverView,
            sessionToken = mediaSession.sessionToken
        )
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
            val state = mediaController.playbackState ?: return
            updateNotification(state)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state ?: return
            updateNotification(state)
        }

        private fun updateNotification(state: PlaybackStateCompat) = lifecycleScope.launch {
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
                            stopForegroundJob?.cancel()
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

    private fun handleLastSessionSysMediaButton() = lifecycleScope.launch {
        injector.analytics.logEvent(START_PLAYER_FORM_LAST_SESSION, null)
        val recentlyPlayedSongs = injector.getRecentlyPlayedSongs
        val recentTracks = recentlyPlayedSongs()
        if (recentTracks.isNotEmpty()) {
            PlayerQueue.playTrack(recentTracks[0], recentTracks)
            delay(500)
            if (!MusicApp.get().isInForeground) {
                VideoEmplacementLiveData.out()
            }
        }
    }

    // Schedule stop foreground service if not playing, so user can swipe to delete notification
    private var stopForegroundJob: Job? = null
    private fun scheduleStopForeground() {
        stopForegroundJob?.cancel()
        stopForegroundJob = lifecycleScope.launch {
            delay(5 * 1000) // 5 seconds
            if (mediaController.playbackState?.isPlaying == true) return@launch
            stopForeground(false)
        }
    }

    fun getPlayerView(): YouTubePlayerView {
        return floatingPlayerView.playerView()
    }

    inner class ServiceBinder : Binder() {
        fun service() = this@MusicPlayerService
    }

    companion object {
        const val COMMAND_PLAY_TRACK = "video-id"
        const val COMMAND_RESUME = "resume"
        const val COMMAND_PAUSE = "pause"
        const val COMMAND_SEEK_TO = "seek-to"
        const val COMMAND_SCHEDULE_TIMER = "schedule-timer"

        object CustomAction {
            const val ADD_TO_FAVOURITE = "add_current_media_to_Favourite"
            const val REMOVE_FROM_FAVOURITE = "remove_current_media_from_Favourite"
        }

        object CustomCommand {
            const val DISABLE_NOTIFICATION_ACTIONS = "disable_notification_actions"
            const val ENABLE_NOTIFICATION_ACTIONS = "enable_notification_actions"
        }
    }
}


// Analytics
private const val START_PLAYER_FORM_LAST_SESSION = "player_started_by_last_session"


