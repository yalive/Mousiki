package com.mousiki.shared.ui.playlist

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.repository.AudioRepository
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.song.GetPlaylistVideosUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import com.mousiki.shared.ui.resource.songList
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistSongsViewModel(
    private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate,
    private val audioRepository: AudioRepository
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _songs = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val songs: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _songs

    fun init(playlistId: String) {
        getPlaylistSongs(playlistId)
    }

    private fun getPlaylistSongs(playlistId: String) = scope.launch {
        _songs.value = Resource.Loading
        val result = getPlaylistVideosUseCase(playlistId)
        _songs.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        populateAdsIn(_songs)
    }

    fun onClickTrack(track: MusicTrack) = scope.launch {
        playTrackFromQueue(track, _songs.songList())
    }

    fun onClickTrackPlayAll() {
        scope.launch {
            val allSongs = _songs.songList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }

    // For iOS
    fun songsFlow(): CommonFlow<Resource<List<DisplayableItem>>?> {
        return songs.asCommonFlow()
    }
}