package com.cas.musicplayer.ui.playlist.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.song.GetPlaylistVideosUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.common.ads.GetListAdsDelegate
import com.cas.musicplayer.ui.common.songList
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
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {


    @AssistedInject.Factory
    interface Factory {
        fun create(playlistId: String): PlaylistSongsViewModel
    }

    private val _songs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val songs: LiveData<Resource<List<DisplayableItem>>>
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
        insertAds(_songs)
    }

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, _songs.songList())
    }

    fun onClickTrackPlayAll() {
        uiCoroutine {
            val allSongs = _songs.songList()
            if (allSongs.isEmpty()) return@uiCoroutine
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }
}
