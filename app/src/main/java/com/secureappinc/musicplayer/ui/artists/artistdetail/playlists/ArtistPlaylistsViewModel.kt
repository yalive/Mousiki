package com.secureappinc.musicplayer.ui.artists.artistdetail.playlists

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.repository.ArtistsRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel(private val repository: ArtistsRepository) : BaseViewModel() {

    val playlists = MutableLiveData<Resource<List<Playlist>>>()

    fun loadPlaylist(channelId: String) = uiScope.launch(coroutineContext) {
        playlists.value = Resource.loading()
        val resource = repository.getPlaylists(channelId)
        playlists.value = resource
    }
}