package com.cas.musicplayer.ui.genres.detailgenre.videos

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.repository.GenresRepository
import com.cas.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenreVideosViewModel @Inject constructor(val repository: GenresRepository) : BaseViewModel() {

    val tracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadTopTracks(topTracksPlaylist: String) = uiScope.launch(coroutineContext) {
        tracks.value = Resource.loading()
        val resource = repository.getTopTracks(topTracksPlaylist)
        tracks.value = resource
    }
}