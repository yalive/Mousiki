package com.cas.musicplayer.ui.local.videos.player

import android.app.Application
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.cas.musicplayer.MusicApp
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import java.lang.RuntimeException

class VideoPlayerViewModel(application: Application) : AndroidViewModel(application) {


    var player: SimpleExoPlayer? = null
    var loudnessEnhancer: LoudnessEnhancer? = null

    var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    var currentUri: Uri? = null
    var currentId: Long = 0L

    init {
        initializePlayer()
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(getApplication() as MusicApp).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = SimpleExoPlayer.Builder(getApplication() as MusicApp)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
        try {
            loudnessEnhancer = LoudnessEnhancer(player?.audioSessionId!!)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun start() {
        val mediaItem = MediaItem.Builder()
            .setUri(currentUri)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = true
        playWhenReady = true
    }

    fun stop() {
        playWhenReady = false
        player?.pause()
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = false
            release()
        }
        player = null
    }
}

