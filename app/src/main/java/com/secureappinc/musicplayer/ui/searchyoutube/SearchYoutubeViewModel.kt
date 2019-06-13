package com.secureappinc.musicplayer.ui.searchyoutube

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.data.enteties.Channel
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.repository.SearchRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import com.secureappinc.musicplayer.utils.getCurrentLocale
import com.secureappinc.musicplayer.utils.getLanguage
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel(val searchRepository: SearchRepository) : BaseViewModel() {

    val videos = MutableLiveData<Resource<List<MusicTrack>>>()
    val channels = MutableLiveData<Resource<List<Channel>>>()
    val playlists = MutableLiveData<Resource<List<Playlist>>>()

    val searchSuggestions = MutableLiveData<List<String>>()

    var lastQuery = ""

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
        videos.value = Resource.loading()
        val resource = searchRepository.searchTracks(query)
        videos.value = resource
        println()
    }

    private suspend fun loadPlaylists(query: String) {
        playlists.value = Resource.loading()
        val resource = searchRepository.searchPlaylists(query)
        playlists.value = resource
        println()
    }

    private suspend fun loadChannels(query: String) {
        channels.value = Resource.loading()
        val resource = searchRepository.searchChannels(query)
        channels.value = resource
        println()
    }

    fun getSuggestions(query: String) = uiScope.launch(coroutineContext) {
        val url =
            "https://clients1.google.com/complete/search?client=youtube&hl=${getLanguage()}&ql=${getCurrentLocale()}&gs_rn=23&gs_ri=youtube&ds=yt&cp=${query.length}&q=${query.replace(
                " ",
                "+"
            )}"

        val suggestionList = searchRepository.getSuggestions(url)
        if (suggestionList.isNotEmpty()) {
            searchSuggestions.value = suggestionList
        }
    }
}