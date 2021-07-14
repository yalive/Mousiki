package com.cas.musicplayer.ui.local.playlists

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.customplaylist.GetLocalPlaylistItemCountUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetLocalPlaylistsFlowUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalPlaylistsViewModel(
    private val getLocalPlaylistsFlow: GetLocalPlaylistsFlowUseCase,
    private val getLocalPlaylistItemCount: GetLocalPlaylistItemCountUseCase,
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
            _playlists.value = localPlaylists
            jobCounts?.cancel()
            jobCounts = launch { observePlaylistItemsCount(localPlaylists) }
        }
    }

    private fun CoroutineScope.observePlaylistItemsCount(playlists: List<Playlist>) {
        playlists.forEach { pList ->
            launch {
                getLocalPlaylistItemCount(pList).collect { count ->
                    _playlists.value = _playlists.value?.map {
                        if (it.id == pList.id) it.copy(itemCount = count) else it
                    }
                }
            }
        }
    }
}