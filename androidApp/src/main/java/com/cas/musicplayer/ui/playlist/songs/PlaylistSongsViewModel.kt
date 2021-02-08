package com.cas.musicplayer.ui.playlist.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.cas.musicplayer.ui.common.ads.GetListAdsDelegate
import com.cas.musicplayer.ui.common.songList
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.song.GetPlaylistVideosUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistSongsViewModel(
    private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _songs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val songs: LiveData<Resource<List<DisplayableItem>>>
        get() = _songs

    fun init(playlistId: String) {
        getPlaylistSongs(playlistId)
    }

    private fun getPlaylistSongs(playlistId: String) = viewModelScope.launch {
        _songs.value = Resource.Loading
        val result = getPlaylistVideosUseCase(playlistId)
        _songs.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        populateAdsIn(_songs)
    }

    fun onClickTrack(track: MusicTrack) = viewModelScope.launch {
        playTrackFromQueue(track, _songs.songList())
    }

    fun onClickTrackPlayAll() {
        viewModelScope.launch {
            val allSongs = _songs.songList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }
}
