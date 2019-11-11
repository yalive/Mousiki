package com.cas.musicplayer.ui.genres.detailgenre.playlists

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.repository.GenresRepository
import com.cas.musicplayer.ui.home.ui.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenrePlaylistsViewModel @Inject constructor(val repository: GenresRepository) : BaseViewModel() {

    val playlists = MutableLiveData<ResourceOld<List<Playlist>>>()

    fun loadTopTracks(channelId: String) = uiScope.launch(coroutineContext) {
        playlists.value = ResourceOld.loading()
        val resource = repository.getPlaylists(channelId)
        playlists.value = resource
    }
}