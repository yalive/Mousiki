package com.cas.musicplayer.player.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.cas.common.extensions.bool
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.*
import com.cas.musicplayer.player.extensions.albumArt
import com.cas.musicplayer.player.extensions.isPlaying
import com.cas.musicplayer.player.extensions.musicTrack
import com.cas.musicplayer.player.receiver.BecomingNoisyReceiver
import com.cas.musicplayer.player.receiver.DeleteNotificationReceiver
import com.cas.musicplayer.player.receiver.FavouriteReceiver
import com.cas.musicplayer.player.receiver.LockScreenReceiver
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.imgUrl
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

    // Player
    private val sessionHandler by lazy { MediaSessionHandlerImpl(mediaSession) }
    private val ytbPlayer by lazy { YTBPlayer(mediaController, sessionHandler) }
    private val player by lazy {
        val localPlayer = LocalPlayer(
            context = this,
            scope = lifecycleScope,
            mediaSessionHandler = sessionHandler,
            localSongsRepository = Injector.localSongsRepository
        )
        MultiPlayer(ytbPlayer, localPlayer)
    }

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
    private lateinit var bottomViewParams: WindowManager.LayoutParams
    private lateinit var batterySaverView: View
    private lateinit var batterySaverViewParams: WindowManager.LayoutParams
    private lateinit var floatingPlayerView: YoutubeFloatingPlayerView
    private var addedViewsToWindow = false

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

        PlayerQueue.observe(this) { currentTrack ->
            floatingPlayerView.isInvisible = currentTrack is LocalSong
        }
    }

    override fun onBind(intent: Intent): IBinder {
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
            PlayerCommand.CueTrack -> cueCurrentTrack()
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
                player.play()
            }

            override fun onPause() {
                player.pause()
            }

            override fun onStop() {
                stopForeground(true)
            }

            override fun onSeekTo(pos: Long) {
                player.seekTo(pos.toFloat() / 1000)
            }

            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                val videoId = mediaId ?: return
                val cue = extras?.getBoolean("cue", false) ?: false
                if (cue) {
                    player.cueVideo(videoId)
                } else {
                    player.loadVideo(videoId, 0f)
                }
            }

            override fun onSkipToNext() {
                PlayerQueue.playNextTrack()
            }

            override fun onSkipToPrevious() {
                PlayerQueue.playPreviousTrack()
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                val metadata: MediaMetadataCompat = mediaSession.controller.metadata ?: return
                lifecycleScope.launch(Dispatchers.IO) {
                    if (action == CustomAction.ADD_TO_FAVOURITE) {
                        val track = PlayerQueue.value
                        if (track != null) {
                            Injector.addSongToFavourite(track)
                        }
                    } else if (action == CustomAction.REMOVE_FROM_FAVOURITE) {
                        val mediaId = metadata.description.mediaId
                        mediaId?.let {
                            Injector.removeSongFromFavouriteList(mediaId)
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

    private var loadNotificationImage: Job? = null
    private fun moveToForeground() = lifecycleScope.launch {
        val notification = notificationBuilder.buildNotification(mediaSession.sessionToken, true)
        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
        startForeground(NOW_PLAYING_NOTIFICATION, notification)

        // buildNotification with image
        loadNotificationImage?.cancel()
        loadNotificationImage = launch {
            val notificationWithImage =
                notificationBuilder.buildNotification(mediaSession.sessionToken)
            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notificationWithImage)
        }
        try {
            loadNotificationImage?.join()
        } catch (e: Exception) {
        }
    }

    private fun playCurrentTrack() {
        val currentTrack = PlayerQueue.value ?: return
        metadataBuilder.musicTrack = currentTrack
        mediaSession.setMetadata(metadataBuilder.build())
        if (currentTrack.id.isEmpty()) return
        mediaController.transportControls.playFromMediaId(currentTrack.id, null)

        lifecycleScope.launch(Dispatchers.Main) {
            Injector.addTrackToRecentlyPlayed(currentTrack)
            val loadBitmap = Picasso.get().getBitmap(currentTrack.imgUrl, 320)
            metadataBuilder.albumArt = loadBitmap
            mediaSession.setMetadata(metadataBuilder.build())
        }
    }

    private fun cueCurrentTrack() {
        val currentTrack = PlayerQueue.value ?: return
        metadataBuilder.musicTrack = currentTrack
        mediaSession.setMetadata(metadataBuilder.build())
        if (currentTrack.id.isEmpty()) return
        mediaController.transportControls.playFromMediaId(
            currentTrack.id, bundleOf("cue" to true)
        )
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
        if (intent.bool(COMMAND_PLAY)) return PlayerCommand.PlayTrack
        if (intent.bool(COMMAND_CUE)) return PlayerCommand.CueTrack
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

            // Ensure views are added to window manager
            if (!addedViewsToWindow && canDrawOverApps()) {
                addedViewsToWindow = true
                windowManager.addView(bottomView, bottomViewParams)
                windowManager.addView(batterySaverView, batterySaverViewParams)
            }
        })
    }

    private fun addBottomView() {
        bottomView = LayoutInflater.from(this)
            .inflate(R.layout.bottom_floating_player, null)
            .apply { isVisible = false }

        bottomViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowOverlayTypeOrPhone,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        bottomViewParams.gravity = Gravity.BOTTOM or Gravity.START
        bottomViewParams.x = 0
        bottomViewParams.y = 0

        if (canDrawOverApps()) {
            addedViewsToWindow = true
            windowManager.addView(bottomView, bottomViewParams)
        }
    }

    private fun createBatterySaverView() {
        batterySaverView = LayoutInflater.from(this)
            .inflate(R.layout.battery_saver_floating_view, null)
            .apply { isVisible = false }
        batterySaverViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowOverlayTypeOrPhone,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        batterySaverViewParams.gravity = Gravity.TOP or Gravity.START
        batterySaverViewParams.x = 0
        batterySaverViewParams.y = 0

        if (canDrawOverApps()) {
            addedViewsToWindow = true
            windowManager.addView(batterySaverView, batterySaverViewParams)
        }
    }

    private fun createFloatingPlayerView() {
        floatingPlayerView = YoutubeFloatingPlayerView(
            ContextThemeWrapper(this, R.style.Theme_MaterialComponents)
        )

        floatingPlayerView.preparePlayerView(
            service = this,
            ytbPlayer = ytbPlayer,
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

            // Ensure floating video is visible
            val track = PlayerQueue.value
            if (state.isPlaying && track is YtbTrack) {
                floatingPlayerView.isVisible = true
            }
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
        Injector.analytics.logEvent(START_PLAYER_FORM_LAST_SESSION)
        val recentlyPlayedSongs = Injector.getRecentlyPlayedSongs
        val recentTracks = recentlyPlayedSongs()
        if (recentTracks.isNotEmpty()) {
            PlayerQueue.playTrack(recentTracks[0], recentTracks)
            delay(500)
            if (!MusicApp.get().isInForeground) {
                VideoEmplacementLiveData.out()
            }
        }
    }

    fun getPlayerView(): YouTubePlayerView {
        return floatingPlayerView.playerView()
    }

    inner class ServiceBinder : Binder() {
        fun service() = this@MusicPlayerService
    }

    companion object {
        const val COMMAND_PLAY = "play"
        const val COMMAND_RESUME = "resume"
        const val COMMAND_PAUSE = "pause"
        const val COMMAND_CUE = "prepare_cue"
        const val COMMAND_SEEK_TO = "seek-to"
        const val COMMAND_SCHEDULE_TIMER = "schedule-timer"

        object CustomAction {
            const val ADD_TO_FAVOURITE = "add_current_media_to_Favourite"
            const val REMOVE_FROM_FAVOURITE = "remove_current_media_from_Favourite"
        }
    }
}


// Analytics
private const val START_PLAYER_FORM_LAST_SESSION = "player_started_by_last_session"


