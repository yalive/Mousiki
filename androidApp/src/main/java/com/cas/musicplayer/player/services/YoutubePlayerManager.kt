package com.cas.musicplayer.player.services

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.player.MousikiPlayer
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.toast
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/22/20.
 ***************************************
 */

class YoutubePlayerManager(
    private val mediaController: MediaControllerCompat,
    private val mediaSession: MediaSessionCompat,
) : AbstractYouTubePlayerListener(), MousikiPlayer {

    private var youTubePlayer: YouTubePlayer? = null
    private var playbackstateBuilder = PlaybackStateCompat.Builder()
    private var elapsedSeconds: Int = 0
    private var seekToCalled = false
    private var stateBeforeSeek: PlayerConstants.PlayerState? = null
    private var requestCue = false

    override fun onReady(youTubePlayer: YouTubePlayer) {
        this.youTubePlayer = youTubePlayer
        if (requestCue) {
            requestCue = false
            val track = PlayerQueue.value ?: return
            youTubePlayer.cueVideo(track.youtubeId, 0f)
            return
        }
        PlayerQueue.value?.let { currentTrack ->
            mediaController.transportControls?.playFromMediaId(
                currentTrack.youtubeId,
                null
            )
        }
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        MusicApp.get().toast(R.string.error_cannot_play_youtube_video)
        if (error == PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER || error == PlayerConstants.PlayerError.VIDEO_NOT_FOUND) {
            // Skip to next on error
            mediaController.transportControls?.skipToNext()
        }
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        PlaybackLiveData.value = state
        if (state == PlayerConstants.PlayerState.ENDED) {
            if (seekToCalled && stateBeforeSeek == PlayerConstants.PlayerState.PAUSED) {
                // Nothing to do
            } else {
                mediaController.transportControls?.skipToNext()
            }
        }
        seekToCalled = false
        updatePlayerState(state)
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
        elapsedSeconds = 0
        youTubePlayer?.loadVideo(videoId, 0f)
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        elapsedSeconds = 0
        requestCue = true
        youTubePlayer?.cueVideo(videoId, 0f)
    }

    override fun play() {
        if (PlaybackLiveData.value == PlayerConstants.PlayerState.ENDED) {
            mediaController.transportControls?.skipToNext()
        } else {
            youTubePlayer?.play()
        }
    }

    override fun pause() {
        youTubePlayer?.pause()
    }

    override fun stop() {
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
    }

    override fun seekTo(time: Float) {
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
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
        }
    }

    fun onScreenLocked() {
        val newBuilder = playbackstateBuilder
        newBuilder.setExtras(
            bundleOf(
                "screenLocked" to true
            )
        )
        mediaSession.setPlaybackState(newBuilder.build())
    }

    fun onScreenUnlocked() {
        val newBuilder = playbackstateBuilder
        newBuilder.setExtras(
            bundleOf(
                "screenLocked" to false
            )
        )
        mediaSession.setPlaybackState(newBuilder.build())
    }

    private fun updatePlayerState(state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.PLAYING -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
            PlayerConstants.PlayerState.PAUSED -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
            else -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }

    private fun setMediaPlaybackState(state: Int) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        } else {
            playbackstateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        }
        val speed = when (state) {
            PlaybackStateCompat.STATE_PLAYING -> 1f
            else -> 0f
        }
        playbackstateBuilder.setState(
            state,
            elapsedSeconds * 1000L,
            speed
        )
        mediaSession.setPlaybackState(playbackstateBuilder.build())
    }
}