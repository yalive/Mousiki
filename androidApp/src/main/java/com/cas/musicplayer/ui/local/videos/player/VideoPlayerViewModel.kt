package com.cas.musicplayer.ui.local.videos.player

import android.content.ContentUris
import android.content.Context
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.folders.Folder
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.cas.musicplayer.utils.fixedPath
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedVideosUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.io.File
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioAttributes


class VideoPlayerViewModel(
    private val appContext: Context,
    private val localVideosRepository: LocalVideosRepository,
    private val getRecentlyPlayedVideos: GetRecentlyPlayedVideosUseCase
) : BaseViewModel() {

    private val _queue = MutableLiveData<List<DisplayedVideoItem>>()
    val queue: LiveData<List<DisplayedVideoItem>> = _queue

    private val _currentVideo = MutableLiveData<DisplayedVideoItem>()
    val currentVideo: LiveData<DisplayedVideoItem> = _currentVideo

    var player: SimpleExoPlayer? = null
    var loudnessEnhancer: LoudnessEnhancer? = null

    var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private var currentUri: Uri? = null

    var currentQueueType: VideoQueueType? = null
        private set

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
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        player = SimpleExoPlayer.Builder(appContext)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
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

    fun playVideo(video: Track) {
        _currentVideo.value = video.toDisplayedVideoItem()
        currentUri =
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                video.id.toLong()
            )
        start()

        _queue.value = _queue.value?.map { it.copy(isCurrent = it.track.id == video.id) }
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

    fun prepareQueue(
        queueType: VideoQueueType,
        video: Track
    ) {
        _currentVideo.value = video.toDisplayedVideoItem()
        currentUri =
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                video.id.toLong()
            )

        // Load queue videos
        if (currentQueueType == queueType) return // Queue unchanged
        currentQueueType = queueType
        viewModelScope.launch {
            _queue.value = when (queueType) {
                VideoQueueType.AllVideos -> localVideosRepository.videos().filterNotHidden().map {
                    LocalSong(it).toDisplayedVideoItem()
                }
                is VideoQueueType.FolderLocation -> localVideosRepository.videos()
                    .filter { File(it.path).fixedPath(MusicApp.get()) == queueType.folder.path }
                    .map { song -> LocalSong(song).toDisplayedVideoItem() }
                VideoQueueType.History -> getRecentlyPlayedVideos(200).map {
                    LocalSong(localVideosRepository.video(it.id.toLong())).toDisplayedVideoItem()
                }
            }.map { it.copy(isCurrent = it.track.id == video.id) }
        }
    }

    fun playNextVideo() {
        val index =
            _queue.value?.indexOfFirst { it.track.id == currentVideo.value?.track?.id.toString() }
        if (index == null || index == -1) return
        val nextVideo = _queue.value?.getOrNull(index + 1) ?: return
        playVideo(nextVideo.track)
    }
}

sealed class VideoQueueType : Parcelable {
    @Parcelize
    object History : VideoQueueType()

    @Parcelize
    data class FolderLocation(val folder: Folder) : VideoQueueType()

    @Parcelize
    object AllVideos : VideoQueueType()
}