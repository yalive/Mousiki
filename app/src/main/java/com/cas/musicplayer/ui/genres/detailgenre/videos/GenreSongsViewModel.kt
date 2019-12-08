package com.cas.musicplayer.ui.genres.detailgenre.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.song.GetPlaylistVideosUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class GenreSongsViewModel @Inject constructor(
    val getPlaylistVideos: GetPlaylistVideosUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

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

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        val tracks = (_tracks.value as? Resource.Success)?.data?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }
}