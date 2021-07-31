package com.cas.musicplayer.ui.local.playlists

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.imgUrlDef0
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.domain.usecase.customplaylist.*
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalPlaylistsViewModel(
    private val getLocalPlaylistsFlow: GetLocalPlaylistsFlowUseCase,
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getLocalPlaylistItemCount: GetLocalPlaylistItemCountUseCase,
    private val removeCustomPlaylist: RemoveCustomPlaylistUseCase,
    private val getCustomPlaylistFirstYtbTrack: CustomPlaylistFirstYtbTrackUseCase,
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

    private suspend fun Playlist.withImage(): Playlist {
        return if (isCustom) {
            val ytbTrack = getCustomPlaylistFirstYtbTrack(id)
            copy(urlImage = ytbTrack?.imgUrlDef0.orEmpty())
        } else this
    }
}