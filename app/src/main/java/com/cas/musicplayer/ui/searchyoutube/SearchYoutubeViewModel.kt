package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.data.enteties.Channel
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.repository.SearchRepository
import com.cas.musicplayer.utils.getCurrentLocale
import com.cas.musicplayer.utils.getLanguage
import com.cas.musicplayer.utils.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel @Inject constructor(val searchRepository: SearchRepository) : BaseViewModel() {

    val videos = MutableLiveData<ResourceOld<List<MusicTrack>>>()
    val channels = MutableLiveData<ResourceOld<List<Channel>>>()
    val playlists = MutableLiveData<ResourceOld<List<Playlist>>>()

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
        videos.value = ResourceOld.loading()
        val resource = searchRepository.searchTracks(query)
        videos.value = resource
        println()
    }

    private suspend fun loadPlaylists(query: String) {
        playlists.value = ResourceOld.loading()
        val resource = searchRepository.searchPlaylists(query)
        playlists.value = resource
        println()
    }

    private suspend fun loadChannels(query: String) {
        channels.value = ResourceOld.loading()
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