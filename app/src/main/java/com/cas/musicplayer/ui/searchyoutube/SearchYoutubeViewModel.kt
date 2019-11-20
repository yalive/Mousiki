package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.common.resource.Resource
import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.domain.model.Playlist
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.domain.usecase.search.GetGoogleSearchSuggestionsUseCase
import com.cas.musicplayer.domain.usecase.search.SearchChannelsUseCase
import com.cas.musicplayer.domain.usecase.search.SearchPlaylistsUseCase
import com.cas.musicplayer.domain.usecase.search.SearchSongsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import com.cas.musicplayer.utils.uiScope
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
    private val getGoogleSearchSuggestions: GetGoogleSearchSuggestionsUseCase
) : BaseViewModel() {

    private val _videos = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val videos: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _videos

    private val _playlists = MutableLiveData<Resource<List<Playlist>>>()
    val playlists: LiveData<Resource<List<Playlist>>>
        get() = _playlists

    private val _channels = MutableLiveData<Resource<List<Channel>>>()
    val channels: LiveData<Resource<List<Channel>>>
        get() = _channels

    private val _searchSuggestions = MutableLiveData<List<String>>()
    val searchSuggestions: LiveData<List<String>>
        get() = _searchSuggestions

    private var lastQuery = ""

    fun search(query: String) = uiScope.launch(coroutineContext) {
        if (lastQuery == query && videos.value != null && channels.value != null && playlists.value != null) {
            return@launch
        }
        lastQuery = query
        launch { loadVideos(query) }
        launch { loadPlaylists(query) }
        launch { loadChannels(query) }
    }

    private suspend fun loadVideos(query: String) {
        _videos.value = Resource.Loading
        val resource = searchSongs(query)
        _videos.value = resource.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }

    private suspend fun loadPlaylists(query: String) {
        _playlists.value = Resource.Loading
        val resource = searchPlaylists(query)
        _playlists.value = resource.asResource()
    }

    private suspend fun loadChannels(query: String) {
        _channels.value = Resource.Loading
        val resource = searchChannels(query)
        _channels.value = resource.asResource()
    }

    fun getSuggestions(query: String) = uiCoroutine {
        val suggestionList = getGoogleSearchSuggestions(query)
        if (suggestionList.isNotEmpty()) {
            _searchSuggestions.value = suggestionList
        }
    }
}