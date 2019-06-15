package com.secureappinc.musicplayer.ui.genres.detailgenre.playlists

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.repository.GenresRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenrePlaylistsViewModel @Inject constructor(val repository: GenresRepository) : BaseViewModel() {

    val playlists = MutableLiveData<Resource<List<Playlist>>>()

    fun loadTopTracks(channelId: String) = uiScope.launch(coroutineContext) {
        playlists.value = Resource.loading()
        val resource = repository.getPlaylists(channelId)
        playlists.value = resource
    }
}