package com.cas.musicplayer.ui.genres.detailgenre.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.common.resource.Resource
import com.cas.musicplayer.domain.model.Playlist
import com.cas.common.result.asResource
import com.cas.musicplayer.domain.usecase.channel.GetChannelPlaylistsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenrePlaylistsViewModel @Inject constructor(
    val getChannelPlaylists: GetChannelPlaylistsUseCase
) : BaseViewModel() {

    private val _playlists = MutableLiveData<Resource<List<Playlist>>>()
    val playlists: LiveData<Resource<List<Playlist>>>
        get() = _playlists

    fun loadTopTracks(channelId: String) = uiCoroutine {
        _playlists.value = Resource.Loading
        val result = getChannelPlaylists(channelId)
        _playlists.value = result.asResource()
    }
}