package com.cas.musicplayer.ui.artists.artistdetail.playlists

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.repository.ArtistsRepository
import com.cas.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel @Inject constructor(private val repository: ArtistsRepository) : BaseViewModel() {

    val playlists = MutableLiveData<Resource<List<Playlist>>>()

    fun loadPlaylist(channelId: String) = uiScope.launch(coroutineContext) {
        playlists.value = Resource.loading()
        val resource = repository.getPlaylists(channelId)
        playlists.value = resource
    }
}