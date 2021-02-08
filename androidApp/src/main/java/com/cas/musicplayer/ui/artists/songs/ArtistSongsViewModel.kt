package com.cas.musicplayer.ui.artists.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.cas.musicplayer.ui.common.ads.GetListAdsDelegate
import com.cas.musicplayer.ui.common.songList
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetArtistSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistSongsViewModel(
    private val getArtistSongs: GetArtistSongsUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _tracks = MutableLiveData<Resource<List<DisplayableItem>>>()
    val tracks: LiveData<Resource<List<DisplayableItem>>>
        get() = _tracks

    fun init(artist: Artist) {
        loadArtistTracks(artist)
    }

    fun loadArtistTracks(artist: Artist) = viewModelScope.launch {
        _tracks.value = Resource.Loading
        val result = getArtistSongs(artist)
        _tracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        populateAdsIn(_tracks)
    }

    fun onClickTrack(track: MusicTrack) = viewModelScope.launch {
        playTrackFromQueue(track, _tracks.songList())
    }

    fun onClickTrackPlayAll() {
        viewModelScope.launch {
            val allSongs = _tracks.songList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }
}