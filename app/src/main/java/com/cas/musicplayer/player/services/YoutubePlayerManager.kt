package com.cas.musicplayer.player.services

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.cas.musicplayer.player.MousikiPlayer
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.isScreenLocked
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
    private val mediaSession: MediaSessionCompat
) : AbstractYouTubePlayerListener(), MousikiPlayer {

    private var youTubePlayer: YouTubePlayer? = null
    private var playbackstateBuilder = PlaybackStateCompat.Builder()

    override fun onReady(youTubePlayer: YouTubePlayer) {
        this.youTubePlayer = youTubePlayer
        PlayerQueue.value?.let { currentTrack ->
            mediaController.transportControls.playFromMediaId(
                currentTrack.youtubeId,
                null
            )
        }
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        PlaybackLiveData.value = state
        if (state == PlayerConstants.PlayerState.ENDED) {
            PlayerQueue.playNextTrack()
        }
        when (state) {
            PlayerConstants.PlayerState.PLAYING, PlayerConstants.PlayerState.BUFFERING -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
            PlayerConstants.PlayerState.PAUSED -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
            else -> {
            }
        }
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        if (isScreenLocked()) {
            PlayerQueue.pause()
            return
        }
        PlaybackDuration.value = second
    }

    private fun setMediaPlaybackState(state: Int) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_PAUSE
                        /*or PlaybackStateCompat.ACTION_SEEK_TO*/
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        } else {
            playbackstateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        /*or PlaybackStateCompat.ACTION_SEEK_TO*/
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        }
        val speed = when (state) {
            PlaybackStateCompat.STATE_PLAYING -> 1f
            else -> 0f
        }
        playbackstateBuilder.setState(
            state,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            speed
        )
        mediaSession.setPlaybackState(playbackstateBuilder.build())
    }

    override fun loadVideo(videoId: String, startSeconds: Float) {
        youTubePlayer?.loadVideo(videoId, 0f)
    }

    override fun play() {
        youTubePlayer?.play()
    }

    override fun pause() {
        youTubePlayer?.pause()
    }

    override fun seekTo(time: Float) {
        youTubePlayer?.seekTo(time)
    }
}