package com.cas.musicplayer.ui.searchyoutube

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.search.GetGoogleSearchSuggestionsUseCase
import com.mousiki.shared.domain.usecase.search.GetRecentSearchQueriesUseCase
import com.mousiki.shared.domain.usecase.search.SaveSearchQueryUseCase
import com.mousiki.shared.domain.usecase.search.SearchSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import com.mousiki.shared.ui.resource.songList
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
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _videos = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val videos: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _videos

    private val _searchSuggestions = MutableStateFlow<List<SearchSuggestion>?>(null)
    val searchSuggestions: StateFlow<List<SearchSuggestion>?>
        get() = _searchSuggestions

    private var lastQuery = ""

    init {
        showHistoricSearch()
    }

    private var searchKey: String? = null
    private var searchToken: String? = null
    private var currentPage: Int = 1

    fun search(query: String) = scope.launch {
        if (lastQuery == query && videos.value != null) {
            return@launch
        }
        currentPage = 1
        searchKey = null
        searchToken = null
        lastQuery = query
        launch { loadVideos(query) }
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
            searchResult.tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        populateAdsIn(_videos)
    }

    fun getSuggestions(keyword: String?) = scope.launch {
        if (keyword == null || keyword.isEmpty() || keyword.length <= 1) {
            showHistoricSearch()
            return@launch
        }

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
    }
}