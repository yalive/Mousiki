package com.secureappinc.musicplayer.ui.genres.detailgenre.videos

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.repository.GenresRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenreVideosViewModel(val repository: GenresRepository) : BaseViewModel() {

    val tracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadTopTracks(topTracksPlaylist: String) = uiScope.launch(coroutineContext) {
        tracks.value = Resource.loading()
        val resource = repository.getTopTracks(topTracksPlaylist)
        tracks.value = resource
    }
}