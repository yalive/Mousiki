package com.cas.musicplayer.ui.artists.artistdetail.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.musicplayer.domain.usecase.artist.GetArtistSongsUseCase
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistVideosViewModel @Inject constructor(
    private val getArtistSongs: GetArtistSongsUseCase
) : BaseViewModel() {

    private val _tracks = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val tracks: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _tracks

    fun loadArtistTracks(channelId: String) = uiCoroutine {
        _tracks.value = Resource.Loading
        val result = getArtistSongs(channelId)
        _tracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }
}