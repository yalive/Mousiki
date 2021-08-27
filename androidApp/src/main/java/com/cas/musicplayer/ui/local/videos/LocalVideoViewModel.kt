package com.cas.musicplayer.ui.local.videos

import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.cas.musicplayer.utils.SongsUtil
import com.cas.musicplayer.utils.Utils
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.*
import java.io.File

class LocalVideoViewModel(
    private val localSongsRepository: LocalVideosRepository,
    private val playSongsDelegate: PlaySongDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>>
        get() = _localSongs

    init {
        loadAllVideos()
    }

    fun loadAllVideos() = viewModelScope.launch {
        val songs = localSongsRepository.videos().filterNotHidden()
        val songsItems = songs.map {
            LocalSong(it).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderVideosActionsItem(songsItems.size))
            addAll(songsItems)
        }
        _localSongs.value = updateCurrentPlaying(displayedItems)

        // cache images if needed
        //syncVideosImages(songs)
    }

    fun onClickTrack(track: Track) = scope.launch {
        val tracks = _localSongs.value
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: return@launch
        playTrackFromQueue(track, tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }

    private fun CoroutineScope.syncVideosImages(songs: List<Song>) = launch(Dispatchers.Default) {
        val cacheDir = File(MusicApp.get().filesDir, SongsUtil.CACHE_IMAGE_DIR)
        if (!cacheDir.exists()) cacheDir.mkdir()
        songs.forEach { song ->
            val file = File(cacheDir, "${song.id}.jpeg")
            if (!file.exists()) {
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.id
                )
                val byteArray = Utils.getVideoThumbnail(uri)
                if (byteArray != null) {
                    file.writeBytes(byteArray)
                }
            }
        }
    }
}