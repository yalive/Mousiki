package com.cas.musicplayer.ui.artists.artistdetail.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.ui.commondomain.GetChannelPlaylistsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel @Inject constructor(
    private val getChannelPlaylists: GetChannelPlaylistsUseCase
) : BaseViewModel() {

    private val _playlists = MutableLiveData<Resource<List<Playlist>>>()
    val playlists: LiveData<Resource<List<Playlist>>>
        get() = _playlists

    fun loadPlaylists(channelId: String) = uiCoroutine {
        _playlists.value = Resource.Loading
        val result = getChannelPlaylists(channelId)
        _playlists.value = result.asResource()
    }
}