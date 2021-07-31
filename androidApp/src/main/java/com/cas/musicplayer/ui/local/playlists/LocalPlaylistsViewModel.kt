package com.cas.musicplayer.ui.local.playlists

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.imgUrl
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetLocalPlaylistItemCountUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetLocalPlaylistsFlowUseCase
import com.mousiki.shared.domain.usecase.customplaylist.RemoveCustomPlaylistUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class LocalPlaylistsViewModel(
    private val getLocalPlaylistsFlow: GetLocalPlaylistsFlowUseCase,
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getLocalPlaylistItemCount: GetLocalPlaylistItemCountUseCase,
    private val removeCustomPlaylist: RemoveCustomPlaylistUseCase,
) : BaseViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlist: LiveData<List<Playlist>> get() = _playlists

    private var jobCounts: Job? = null

    init {
        preparePlaylists()
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun preparePlaylists() = viewModelScope.launch {
        getLocalPlaylistsFlow().collect { localPlaylists ->
            _playlists.value = localPlaylists.map { it.withImage() }
            jobCounts?.cancel()
            jobCounts = launch { observePlaylistItemsCount(localPlaylists) }
        }
    }

    private fun CoroutineScope.observePlaylistItemsCount(playlists: List<Playlist>) {
        playlists.forEach { pList ->
            launch {
                getLocalPlaylistItemCount(pList).collect { newCount ->
                    val previousCount = _playlists.value?.firstOrNull {
                        it.id == pList.id
                    }?.itemCount

                    if (previousCount != newCount) {
                        _playlists.value = _playlists.value?.map {
                            if (it.id == pList.id) it.withImage().copy(itemCount = newCount) else it
                        }
                    }
                }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) = scope.launch {
        removeCustomPlaylist(playlist.id)
        preparePlaylists()
    }

    private suspend fun Playlist.withImage(): Playlist = withContext(Dispatchers.IO) {
        if (isCustom) {
            val tracks = getCustomPlaylistTracks(id)
            val ytbTrack = tracks.firstOrNull { it.type == Track.TYPE_YTB }
            return@withContext copy(urlImage = ytbTrack?.imgUrl.orEmpty())
        } else {
            return@withContext this@withImage
        }
    }
}