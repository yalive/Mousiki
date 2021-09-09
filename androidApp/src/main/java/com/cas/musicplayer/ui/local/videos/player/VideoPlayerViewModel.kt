package com.cas.musicplayer.ui.local.videos.player

import android.content.ContentUris
import android.content.Context
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.mousiki.shared.ui.base.BaseViewModel

class VideoPlayerViewModel(
    private val appContext: Context
) : BaseViewModel() {

    private val _currentVideo = MutableLiveData<Long>()
    val currentVideo: LiveData<Long> = _currentVideo

    var player: SimpleExoPlayer? = null
    var loudnessEnhancer: LoudnessEnhancer? = null

    var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private var currentUri: Uri? = null
    var currentId: Long = 0L

    init {
        initializePlayer()
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(appContext).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = SimpleExoPlayer.Builder(appContext)
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

    fun setCurrentVideo(videoId: Long, startPlayback: Boolean = false) {
        _currentVideo.value = videoId
        currentUri =
            ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId)
        if (startPlayback) start()
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

