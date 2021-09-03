package com.cas.musicplayer.ui.common.multiselection

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.DeleteTrackFromCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromRecentlyPlayedUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MultiSelectTracksViewModel(
    private val initialTrack: Track?,
    private val songs: List<Track>,
    private val deleteTrackFromCustomPlaylist: DeleteTrackFromCustomPlaylistUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val removeSongFromRecentlyPlayed: RemoveSongFromRecentlyPlayedUseCase,
) : BaseViewModel() {

    private val _tracks = MutableStateFlow<List<SelectableTrack>?>(null)
    val tracks: StateFlow<List<SelectableTrack>?> = _tracks

    private val _selectionCount = MutableStateFlow(0)
    val selectionCount: StateFlow<Int> = _selectionCount

    private val _cancelVisible = MutableStateFlow(false)
    val cancelVisible: StateFlow<Boolean> = _cancelVisible

    private val _tracksDeleted = MutableStateFlow<Event<Unit>?>(null)
    val tracksDeleted: StateFlow<Event<Unit>?> = _tracksDeleted

    init {
        prepare()
    }

    private fun prepare() = viewModelScope.launch {
        _tracks.value = songs.map {
            SelectableTrack(it, it == initialTrack)
        }
        _cancelVisible.value = false
        _selectionCount.value = _tracks.value?.count { it.selected } ?: 0
    }

    fun onClickTrack(item: SelectableTrack) {
        _tracks.value = _tracks.value?.map {
            if (it.track == item.track) it.copy(selected = !it.selected) else it
        }
        _selectionCount.value = _tracks.value?.count { it.selected } ?: 0
        _cancelVisible.value = _tracks.value?.size == _selectionCount.value
    }

    fun selectAll() {
        _tracks.value = _tracks.value?.map { it.copy(selected = true) }
        _selectionCount.value = _tracks.value?.count() ?: 0
        _cancelVisible.value = true
    }

    fun deselectAll() {
        _tracks.value = _tracks.value?.map { it.copy(selected = false) }
        _selectionCount.value = 0
        _cancelVisible.value = false
    }

    fun selectedTracks(): List<Track> {
        return _tracks.value?.filter { it.selected }?.map { it.track }.orEmpty()
    }

    fun deleteSelectedTracksFrom(playlist: Playlist) = scope.launch {
        if (playlist.isFavourite) {
            selectedTracks().map {
                async { removeSongFromFavouriteList(it.id) }
            }.awaitAll()
        } else if (playlist.isRecent || playlist.isHeavy) {
            selectedTracks().map {
                async { removeSongFromRecentlyPlayed(it.id) }
            }.awaitAll()
        } else {
            selectedTracks().map {
                async { deleteTrackFromCustomPlaylist(it, playlist.id) }
            }.awaitAll()
        }

        _tracksDeleted.value = Unit.asEvent()
    }
}