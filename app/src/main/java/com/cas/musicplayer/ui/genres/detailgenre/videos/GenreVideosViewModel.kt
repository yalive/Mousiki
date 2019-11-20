package com.cas.musicplayer.ui.genres.detailgenre.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.net.map
import com.cas.musicplayer.ui.commondomain.GetPlaylistVideosUseCase
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.ui.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenreVideosViewModel @Inject constructor(
    val getPlaylistVideos: GetPlaylistVideosUseCase
) : BaseViewModel() {

    private val _tracks = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val tracks: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _tracks

    fun loadTopTracks(topTracksPlaylist: String) = uiCoroutine {
        _tracks.value = Resource.Loading
        val result = getPlaylistVideos(topTracksPlaylist)
        _tracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }
}