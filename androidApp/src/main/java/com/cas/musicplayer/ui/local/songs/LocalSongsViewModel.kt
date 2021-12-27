package com.cas.musicplayer.ui.local.songs

import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.tmp.trigger
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.cas.musicplayer.utils.SongsUtil
import com.cas.musicplayer.utils.Utils
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.resource.songList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class LocalSongsViewModel(
    private val localSongsRepository: LocalSongsRepository,
    private val playSongsDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate,
    GetListAdsDelegate by getListAdsDelegate {

    private val _localSongs = MutableStateFlow<List<DisplayableItem>?>(null)
    val localSongs: StateFlow<List<DisplayableItem>?> get() = _localSongs

    private val _showMultiSelection = MutableLiveData<Event<Unit>>()
    val showMultiSelection: LiveData<Event<Unit>> get() = _showMultiSelection

    init {
        loadAllSongs()
    }

    fun loadAllSongs() = viewModelScope.launch {
        val songs = localSongsRepository.songs().filterNotHidden()
        val songsItems = songs.map {
            LocalSong(it).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderSongsActionsItem(songsItems.size,
                onPlayAllTracks = {
                    if (songsItems.isEmpty()) return@HeaderSongsActionsItem
                    onClickTrack(songsItems[0].track)
                },
                onShuffleAllTracks = { onShufflePlay() },
                onMultiSelectTracks = { _showMultiSelection.trigger() }
            ))
            addAll(songsItems)
        }
        Log.d("LocalSongsViewModel", "loadAllSongs result displayedItems : ${songsItems.size}")
        _localSongs.value = updateCurrentPlaying(displayedItems)

        // cache images if needed
        syncSongsImages(songs)

        // Insert ads
        insertAds()
    }

    private suspend fun insertAds() {
        val ads = getOrAwaitNativeAds(ADS_COUNT)
        if (ads.isEmpty()) return
        val items = _localSongs.value?.filter { it !is AdsItem }.orEmpty().toMutableList()
        items.add(1, ads.first())
        if (ads.size > 1) {
            val nextAd = ads[1]
            if (items.size > 10) {
                items.add(8, nextAd)
            } else if (items.size > 5) {
                items.add(nextAd)
            }
        }
        _localSongs.value = items
    }

    fun onClickTrack(track: Track) = scope.launch {
        val tracks = _localSongs.songList()
        if (tracks.isEmpty()) return@launch
        playTrackFromQueue(track, tracks)
    }

    private fun onShufflePlay() = scope.launch {
        val tracks = _localSongs.songList().shuffled()
        if (tracks.isEmpty()) return@launch
        playTrackFromQueue(tracks.random(), tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }

    private fun CoroutineScope.syncSongsImages(songs: List<Song>) = launch(Dispatchers.Default) {
        val cacheDir = File(MusicApp.get().filesDir, SongsUtil.CACHE_IMAGE_DIR)
        if (!cacheDir.exists()) cacheDir.mkdir()
        songs.forEach { song ->
            val file = File(cacheDir, "${song.id}.jpeg")
            if (!file.exists()) {
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.id
                )
                val byteArray = Utils.getSongThumbnail(uri)
                if (byteArray != null) {
                    file.writeBytes(byteArray)
                }
            }
        }
    }

    companion object {
        private const val ADS_COUNT = 1
    }
}