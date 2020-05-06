package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.usecase.search.*
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
    private val searchPlaylists: SearchPlaylistsUseCase,
    private val searchChannels: SearchChannelsUseCase,
    private val getGoogleSearchSuggestions: GetGoogleSearchSuggestionsUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    private val getRecentSearchQueries: GetRecentSearchQueriesUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _videos = MutableLiveData<Resource<List<DisplayableItem>>>()
    val videos: LiveData<Resource<List<DisplayableItem>>>
        get() = _videos

    private val _playlists = MutableLiveData<Resource<List<Playlist>>>()
    val playlists: LiveData<Resource<List<Playlist>>>
        get() = _playlists

    private val _channels = MutableLiveData<Resource<List<Artist>>>()
    val channels: LiveData<Resource<List<Artist>>>
        get() = _channels

    private val _searchSuggestions = MutableLiveData<List<SearchSuggestion>>()
    val searchSuggestions: LiveData<List<SearchSuggestion>>
        get() = _searchSuggestions

    private var lastQuery = ""

    init {
        showHistoricSearch()
    }

    fun search(query: String) = uiScope.launch(coroutineContext) {
        if (lastQuery == query && videos.value != null) {
            return@launch
        }
        lastQuery = query
        launch { loadVideos(query) }
        saveSearchQuery(query)
    }

    private suspend fun loadVideos(query: String) {
        _videos.value = Resource.Loading
        val resource = searchSongs(query)
        _videos.value = resource.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.map { insertAdsIn(it) }.asResource()
    }

    private suspend fun loadPlaylists(query: String) {
        _playlists.value = Resource.Loading
        val resource = searchPlaylists(query)
        _playlists.value = resource.asResource()
    }

    private suspend fun loadChannels(query: String) {
        _channels.value = Resource.Loading
        val resource = searchChannels(query).map { channels ->
            channels.map {
                Artist(it.title, "US", it.id, it.urlImage)
            }
        }
        _channels.value = resource.asResource()
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