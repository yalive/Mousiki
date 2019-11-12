package com.cas.musicplayer.ui.artists.artistdetail.playlists

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.repository.ArtistsRepository
import com.cas.musicplayer.utils.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel @Inject constructor(private val repository: ArtistsRepository) : BaseViewModel() {

    val playlists = MutableLiveData<ResourceOld<List<Playlist>>>()

    fun loadPlaylist(channelId: String) = uiScope.launch(coroutineContext) {
        playlists.value = ResourceOld.loading()
        val resource = repository.getPlaylists(channelId)
        playlists.value = resource
    }
}