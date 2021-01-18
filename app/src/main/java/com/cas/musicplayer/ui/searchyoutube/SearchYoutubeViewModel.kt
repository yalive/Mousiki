package com.cas.musicplayer.ui.searchyoutube

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.common.resource.Resource
import com.cas.common.result.Result
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.search.GetGoogleSearchSuggestionsUseCase
import com.cas.musicplayer.domain.usecase.search.GetRecentSearchQueriesUseCase
import com.cas.musicplayer.domain.usecase.search.SaveSearchQueryUseCase
import com.cas.musicplayer.domain.usecase.search.SearchSongsUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.common.ads.GetListAdsDelegate
import com.cas.musicplayer.ui.common.songList
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import com.cas.musicplayer.utils.uiScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel @Inject constructor(
    private val searchSongs: SearchSongsUseCase,
    private val getGoogleSearchSuggestions: GetGoogleSearchSuggestionsUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    private val getRecentSearchQueries: GetRecentSearchQueriesUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _videos = MutableLiveData<Resource<List<DisplayableItem>>>()
    val videos: LiveData<Resource<List<DisplayableItem>>>
        get() = _videos

    private val _searchSuggestions = MutableLiveData<List<SearchSuggestion>>()
    val searchSuggestions: LiveData<List<SearchSuggestion>>
        get() = _searchSuggestions

    private var lastQuery = ""

    init {
        showHistoricSearch()
    }

    private var searchKey: String? = null
    private var searchToken: String? = null
    private var currentPage: Int = 1

    fun search(query: String) = uiScope.launch(coroutineContext) {
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

    fun loadMore(page: Int) = viewModelScope.launch {
        if (searchKey == null || searchToken == null) {
            // Ignore if there is no token
            // util if data come from local data base
            return@launch
        }

        if (page > 5) {
            Log.d("load_more_search", "Ignored page $page")
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

    fun getSuggestions(keyword: String?) = uiCoroutine {
        if (keyword == null || keyword.isEmpty() || keyword.length <= 1) {
            showHistoricSearch()
            return@uiCoroutine
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

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        val tracks = _videos.songList()
        playTrackFromQueue(track, tracks)
    }

    fun showHistoricSearch() = uiCoroutine {
        val historicSearch = getRecentSearchQueries("").map {
            SearchSuggestion(it, true)
        }
        _searchSuggestions.value = historicSearch
    }
}