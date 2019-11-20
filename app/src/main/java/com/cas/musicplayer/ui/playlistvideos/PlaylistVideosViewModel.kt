package com.cas.musicplayer.ui.playlistvideos

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
class PlaylistVideosViewModel @Inject constructor(
    private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase
) : BaseViewModel() {

    private val _videos = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val videos: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _videos

    fun getPlaylistVideos(playlistId: String) = uiCoroutine {
        _videos.value = Resource.Loading
        val result = getPlaylistVideosUseCase(playlistId)
        _videos.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }
}