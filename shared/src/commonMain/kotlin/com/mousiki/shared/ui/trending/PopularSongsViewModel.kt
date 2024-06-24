package com.mousiki.shared.ui.trending

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */

class PopularSongsViewModel(
    private val getPopularSongs: GetPopularSongsUseCase,
    private val playSongDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate,
    GetListAdsDelegate by getListAdsDelegate {

    private val _newReleases = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val newReleases: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _newReleases

    init {
        loadTrending()
    }

    private var loadingMore = false

    private fun loadTrending() = scope.launch {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@launch
        }
        loadingMore = true
        _newReleases.loading()
        val result = getPopularSongs(PAGE_SIZE)
        _newReleases.value = result
            .map { tracks -> tracks.toDisplayedVideoItems(playSongDelegate) }
            .asResource()
        populateAdsIn(_newReleases)
        loadingMore = false
        if (result is Result.Success && result.data.size < PAGE_SIZE) {
            loadMoreSongs()
        }
    }

    fun loadMoreSongs() = scope.launch {
        if (loadingMore) return@launch
        val allSongs = _newReleases.songList()
        if (allSongs.isNotEmpty() && allSongs.size < MAX_VIDEOS) {
            loadingMore = true
            _newReleases.appendItems(listOf(LoadingItem), false)
            val result = getPopularSongs(PAGE_SIZE, allSongs.lastOrNull() as? AiTrack)
            if (result is Result.Success) {
                val newPageMapped = result.data.map { it.toDisplayedVideoItem() }
                val itemsWithAds = insertAdsIn(newPageMapped)
                _newReleases.appendItems(itemsWithAds, true)
            } else {
                _newReleases.removeLoading()
            }
            loadingMore = false
        }
    }

    fun onClickTrack(track: Track) = scope.launch {
        playTrackFromQueue(track, _newReleases.songList())
    }

    fun onClickTrackPlayAll() = scope.launch {
        val allSongs = _newReleases.songList()
        if (allSongs.isEmpty()) return@launch
        playTrackFromQueue(allSongs.first(), allSongs)
    }

    fun onClickShufflePlay() = scope.launch {
        val allSongs = _newReleases.songList().shuffled()
        if (allSongs.isEmpty()) return@launch
        playTrackFromQueue(allSongs.first(), allSongs)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _newReleases.valueOrNull() ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _newReleases.value = Resource.Success(updatedList)
    }

    companion object {
        private const val MAX_VIDEOS = 200
        private const val PAGE_SIZE = 25
    }
}