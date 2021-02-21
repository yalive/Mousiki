package com.cas.musicplayer.ui.popular

import com.cas.musicplayer.tmp.*
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.LoadingItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
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
    delegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by delegate, GetListAdsDelegate by getListAdsDelegate {

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
        _newReleases.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }.toMutableList()
        }.asResource()
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
            val result = getPopularSongs(PAGE_SIZE, allSongs.lastOrNull())
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

    fun onClickTrack(track: MusicTrack) = scope.launch {
        playTrackFromQueue(track, _newReleases.songList())
    }

    fun onClickTrackPlayAll() = scope.launch {
        val allSongs = _newReleases.songList()
        if (allSongs.isEmpty()) return@launch
        playTrackFromQueue(allSongs.first(), allSongs)
    }

    companion object {
        private const val MAX_VIDEOS = 200
        private const val PAGE_SIZE = 25
    }
}