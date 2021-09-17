package com.cas.musicplayer.player

import android.content.Intent
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.YtbTrack
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/22/20.
 ***************************************
 */

class YTBPlayer(
    private val mediaController: MediaControllerCompat,
    private val mediaSessionHandler: MediaSessionHandler
) : AbstractYouTubePlayerListener(), MousikiPlayer,
    MediaSessionHandler by mediaSessionHandler {

    private var youTubePlayer: YouTubePlayer? = null
    private var elapsedSeconds: Int = 0
    private var seekToCalled = false
    private var stateBeforeSeek: PlayerConstants.PlayerState? = null
    private var requestCue = false

    override fun onReady(youTubePlayer: YouTubePlayer) {
        Log.d(TAG_PLAYER, "YTB player onReady")
        this.youTubePlayer = youTubePlayer
        val track = PlayerQueue.value ?: return
        if (requestCue) {
            requestCue = false
            youTubePlayer.cueVideo(track.id, 0f)
            return
        }
        if (track is YtbTrack) {
            if (track.id.isEmpty()) return
            mediaController.transportControls?.playFromMediaId(track.id, null)
        }
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        Log.d(TAG_PLAYER, "YTB player onError")
        if (error == PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER || error == PlayerConstants.PlayerError.VIDEO_NOT_FOUND) {
            MusicApp.get().toast(R.string.error_cannot_play_youtube_video)
            // Skip to next on error
            mediaController.transportControls?.skipToNext()
        }
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        Log.d(TAG_PLAYER, "YTB player onStateChange $state")
        if (PlayerQueue.value !is YtbTrack) return
        PlaybackLiveData.value = state
        if (state == PlayerConstants.PlayerState.ENDED) {
            if (seekToCalled && stateBeforeSeek == PlayerConstants.PlayerState.PAUSED) {
                // Nothing to do
            } else {
                mediaController.transportControls?.skipToNext()
            }
        }
        seekToCalled = false
        updateMediaSessionState(state)
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        Log.d(TAG_PLAYER, "onVideoDuration: $duration")
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        Log.d(TAG_PLAYER, "onVideoId: $videoId")
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        if (seekToCalled) return // Ignore if seekTo being called, and wait effect of seekTo.
        elapsedSeconds = second.toInt()
        val previousSeconds = PlaybackDuration.value ?: 0
        if (elapsedSeconds != previousSeconds) {
            PlaybackDuration.value = elapsedSeconds
        }
    }

    override fun loadVideo(videoId: String, startSeconds: Float) {
        if (SystemSettings.canEnterPiPMode() && !isScreenLocked() && !MusicApp.get().isInForeground) {
            Log.d(TAG_PLAYER, "YTB player loadVideo, will force PIP")
            enterPipMode()
            youTubePlayer?.loadVideo(videoId, 0f)
            return
        }
        if (!ytbPolicyRespected()) return
        if (!MusicApp.get().isInForeground) {
            VideoEmplacementLiveData.out()
        }
        Log.d(TAG_PLAYER, "YTB player loadVideo")
        elapsedSeconds = 0
        youTubePlayer?.loadVideo(videoId, 0f)
    }

    private fun enterPipMode() {
        // Force activity in PIP mode
        val intent = Intent(MusicApp.get(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MainActivity.EXTRA_START_PIP, true)
        }
        MusicApp.get().startActivity(intent)
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        Log.d(TAG_PLAYER, "YTB player cueVideo")
        elapsedSeconds = 0
        requestCue = true
        youTubePlayer?.cueVideo(videoId, 0f)
    }

    override fun play() {
        if (SystemSettings.canEnterPiPMode() && !isScreenLocked() && !MusicApp.get().isInForeground) {
            Log.d(TAG_PLAYER, "YTB player play, will force PIP")
            enterPipMode()
            return
        }
        if (!ytbPolicyRespected()) return
        if (!MusicApp.get().isInForeground) {
            VideoEmplacementLiveData.out()
        }
        Log.d(TAG_PLAYER, "YTB player play (isInForeground:${MusicApp.get().isInForeground})")
        if (PlaybackLiveData.value == PlayerConstants.PlayerState.ENDED) {
            mediaController.transportControls?.skipToNext()
        } else {
            youTubePlayer?.play()
        }
    }

    override fun pause() {
        Log.d(TAG_PLAYER, "YTB player pause")
        youTubePlayer?.pause()
    }

    override fun stop() {
        Log.d(TAG_PLAYER, "YTB player stop")
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED, elapsedSeconds * 1000L)
    }

    override fun seekTo(time: Float) {
        if (!ytbPolicyRespected()) return
        Log.d(TAG_PLAYER, "YTB player seekTo $time")
        stateBeforeSeek = PlaybackLiveData.value
        elapsedSeconds = time.toInt()
        seekToCalled = true
        youTubePlayer?.seekTo(time)
        PlaybackDuration.value = elapsedSeconds

        // When player is paused, seekTo may not causing onStateChange to be called
        // We need to update notification seek bar specifically when change come from player fragment
        // In case player is not paused calling seekTo will invoke onStateChange and notification will be updated
        val currentState = PlaybackLiveData.value ?: return
        if (currentState == PlayerConstants.PlayerState.ENDED || currentState == PlayerConstants.PlayerState.PAUSED) {
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED, elapsedSeconds * 1000L)
        }
    }

    private fun updateMediaSessionState(state: PlayerConstants.PlayerState) {
        val playbackState = when (state) {
            PlayerConstants.PlayerState.PLAYING -> PlaybackStateCompat.STATE_PLAYING
            PlayerConstants.PlayerState.PAUSED -> PlaybackStateCompat.STATE_PAUSED
            else -> PlaybackStateCompat.STATE_PAUSED
        }
        setMediaPlaybackState(playbackState, elapsedSeconds * 1000L)
    }

    private fun ytbPolicyRespected(): Boolean {
        if (isScreenLocked()) return false
        val context = MusicApp.get()
        if (SystemSettings.canEnterPiPMode()) return true /*context.isInForeground*/
        return context.canDrawOverApps() || context.isInForeground
    }
}