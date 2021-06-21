package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.models.toDisplayedVideoItems
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.search.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import com.mousiki.shared.ui.resource.songList
import com.mousiki.shared.ui.resource.valueOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel(
    private val searchSongs: SearchSongsUseCase,
    private val getGoogleSearchSuggestions: GetGoogleSearchSuggestionsUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    private val getRecentSearchQueries: GetRecentSearchQueriesUseCase,
    private val removeSearchQuery: RemoveSearchQueryUseCase,
    private val clearSearchHistory: ClearSearchHistoryUseCase,
    private val playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _videos = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val videos: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _videos

    private val _searchSuggestions = MutableStateFlow<List<SearchSuggestion>?>(null)
    val searchSuggestions: StateFlow<List<SearchSuggestion>?>
        get() = _searchSuggestions

    private val _hideSearchLoading = MutableStateFlow<Event<Unit>?>(null)
    val hideSearchLoading: StateFlow<Event<Unit>?> get() = _hideSearchLoading

    private val _clearHistoryVisible = MutableStateFlow<Event<Boolean>?>(null)
    val clearHistoryVisible: StateFlow<Event<Boolean>?> get() = _clearHistoryVisible

    private var lastQuery = ""

    init {
        showHistoricSearch()
    }

    private var searchKey: String? = null
    private var searchToken: String? = null
    private var currentPage: Int = 1

    fun search(query: String) = scope.launch {
        if (lastQuery == query && videos.value != null) {
            hideSearchLoadingIndicator()
            return@launch
        }
        _clearHistoryVisible.value = Event(false)
        currentPage = 1
        searchKey = null
        searchToken = null
        lastQuery = query
        loadVideos(query)
        saveSearchQuery(query)
    }

    fun loadMore(page: Int) = scope.launch {
        if (searchKey == null || searchToken == null) {
            // Ignore if there is no token
            // util if data come from local data base
            return@launch
        }

        if (page > 5) {
            //Log.d("load_more_search", "Ignored page $page")
            return@launch
        }

        currentPage = page
        val result = searchSongs(lastQuery, searchKey, searchToken)
        if (result is Result.Success) {
            val searchResult = result.data
            searchKey = searchResult.key
            searchToken = searchResult.token

            val trackList = (_videos.value as? Resource.Success)?.data?.toMutableList()
            if (!trackList.isNullOrEmpty()) {
                val newTracksPage = searchResult.tracks.map { it.toDisplayedVideoItem() }
                val newTracksPageWithAds = insertAdsIn(newTracksPage)
                trackList.addAll(newTracksPageWithAds)
                _videos.value = Resource.Success(trackList)
            }
        }
    }

    private suspend fun loadVideos(query: String) {
        _videos.value = Resource.Loading
        val result = searchSongs(query)
        _videos.value = result.map { searchResult ->
            searchKey = searchResult.key
            searchToken = searchResult.token
            searchResult.tracks.toDisplayedVideoItems(playDelegate)
        }.asResource()
        populateAdsIn(_videos)
    }

    fun getSuggestions(keyword: String?) = scope.launch {
        if (keyword == null || keyword.isEmpty() || keyword.length <= 1) {
            showHistoricSearch()
            return@launch
        }

        _clearHistoryVisible.value = Event(false)
        val suggestionList = async {
            getGoogleSearchSuggestions(keyword).map { SearchSuggestion(it) }
        }
        val localSuggestion = async {
            getRecentSearchQueries(keyword).map { SearchSuggestion(it, true) }
        }
        val allSuggestions = localSuggestion.await() + suggestionList.await()
        if (allSuggestions.isNotEmpty()) {
            _searchSuggestions.value = allSuggestions
        }
    }

    fun onClickTrack(track: MusicTrack) = scope.launch {
        val tracks = _videos.songList()
        playTrackFromQueue(track, tracks)
    }

    fun showHistoricSearch() = scope.launch {
        val historicSearch = getRecentSearchQueries("").map {
            SearchSuggestion(it, true)
        }
        _searchSuggestions.value = historicSearch
        _clearHistoryVisible.value = Event(historicSearch.isNotEmpty())
    }

    private fun hideSearchLoadingIndicator() {
        _hideSearchLoading.value = Event(Unit)
    }

    fun removeHistoricItem(suggestion: SearchSuggestion) {
        _searchSuggestions.value = _searchSuggestions.value?.filter {
            it != suggestion
        }
        viewModelScope.launch {
            removeSearchQuery(suggestion.value)
        }
        if (_searchSuggestions.value.orEmpty().isEmpty()) {
            _clearHistoryVisible.value = Event(false)
        }
    }

    fun clearUserSearchHistory() = viewModelScope.launch {
        clearSearchHistory()
        _searchSuggestions.value = emptyList()
        _clearHistoryVisible.value = Event(false)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _videos.valueOrNull() ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _videos.value = Resource.Success(updatedList)
    }
}