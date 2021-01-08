package com.cas.musicplayer.player.services

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.os.bundleOf
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.player.MousikiPlayer
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.player.TAG_SERVICE
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
    private val mediaSession: MediaSessionCompat
) : AbstractYouTubePlayerListener(), MousikiPlayer {

    private var youTubePlayer: YouTubePlayer? = null
    private var playbackstateBuilder = PlaybackStateCompat.Builder()

    override fun onReady(youTubePlayer: YouTubePlayer) {
        //Log.d(TAG_SERVICE, "onReady: YT player")
        this.youTubePlayer = youTubePlayer
        PlayerQueue.value?.let { currentTrack ->
            Log.d(TAG_SERVICE, "onReady: will call play latest")
            mediaController.transportControls?.playFromMediaId(
                currentTrack.youtubeId,
                null
            )
        }
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        //Log.d(TAG_SERVICE, "onError: YT player")
        MusicApp.get().toast(R.string.error_cannot_play_youtube_video)
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        //Log.d(TAG_SERVICE, "onStateChange: YT player (${state.name})")
        PlaybackLiveData.value = state
        if (state == PlayerConstants.PlayerState.ENDED) {
            mediaController.transportControls?.skipToNext()
        }
        when (state) {
            PlayerConstants.PlayerState.PLAYING, PlayerConstants.PlayerState.BUFFERING -> {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
            PlayerConstants.PlayerState.PAUSED -> {
                Log.d(TAG_SERVICE, "pause: YT player")
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
            else -> {
            }
        }
    }


    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        val seconds = second.toInt()
        val duration = PlaybackDuration.value ?: 0
        if (seconds != duration) {
            PlaybackDuration.value = seconds
        }
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
        //Log.d(TAG_SERVICE, "loadVideo: YT player")
        youTubePlayer?.loadVideo(videoId, 0f)
    }

    override fun play() {
        //Log.d(TAG_SERVICE, "play: YT player")
        youTubePlayer?.play()
    }

    override fun pause() {
        //Log.d(TAG_SERVICE, "pause: YT player")
        youTubePlayer?.pause()
    }

    override fun stop() {
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
    }

    override fun seekTo(time: Float) {
        //Log.d(TAG_SERVICE, "seekTo: YT player")
        youTubePlayer?.seekTo(time)
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
}