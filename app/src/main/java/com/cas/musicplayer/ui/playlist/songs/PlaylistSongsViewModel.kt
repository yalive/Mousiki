package com.cas.musicplayer.ui.playlist.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistSongsViewModel @AssistedInject constructor(
    private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase,
    @Assisted private val playlistId: String,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {


    @AssistedInject.Factory
    interface Factory {
        fun create(playlistId: String): PlaylistSongsViewModel
    }

    private val _songs = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val songs: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _songs

    init {
        getPlaylistSongs()
    }

    private fun getPlaylistSongs() = uiCoroutine {
        _songs.value = Resource.Loading
        val result = getPlaylistVideosUseCase(playlistId)
        _songs.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }

    private fun getChartSongs() = uiCoroutine {
        _songs.value = Resource.Loading
        val result = getPlaylistVideosUseCase(playlistId)
        _songs.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        val tracks = (_songs.value as? Resource.Success)?.data?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickTrackPlayAll() {
        uiCoroutine {
            val allSongs = _songs.valueOrNull() ?: emptyList()
            if (allSongs.isEmpty()) return@uiCoroutine
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }
}