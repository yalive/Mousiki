package com.cas.musicplayer.ui.playlistvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.musicplayer.domain.usecase.song.GetPlaylistVideosUseCase
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
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