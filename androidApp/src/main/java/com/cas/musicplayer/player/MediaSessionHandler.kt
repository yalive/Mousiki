package com.cas.musicplayer.player

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

interface MediaSessionHandler {
    fun setMediaPlaybackState(state: Int, elapsedSeconds: Long)
}

class MediaSessionHandlerImpl(
    private val mediaSession: MediaSessionCompat,
) : MediaSessionHandler {

    private var stateBuilder = PlaybackStateCompat.Builder()

    override fun setMediaPlaybackState(state: Int, elapsedSeconds: Long) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            stateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        } else {
            stateBuilder.setActions(
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
        stateBuilder.setState(state, elapsedSeconds, speed)
        mediaSession.setPlaybackState(stateBuilder.build())
    }
}