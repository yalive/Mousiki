package com.mousiki.shared.ui.artist.songs

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItems
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetArtistSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import com.mousiki.shared.ui.resource.songList
import com.mousiki.shared.ui.resource.valueOrNull
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
class ArtistSongsViewModel(
    private val getArtistSongs: GetArtistSongsUseCase,
    private val playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _tracks = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val tracks: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _tracks

    fun init(artist: Artist) {
        loadArtistTracks(artist)
    }

    private fun loadArtistTracks(artist: Artist) = scope.launch {
        _tracks.value = Resource.Loading
        _tracks.value = getArtistSongs(artist)
            .map { tracks -> tracks.toDisplayedVideoItems(playDelegate) }
            .asResource()
        populateAdsIn(_tracks)
    }

    fun onClickTrack(track: Track) = scope.launch {
        playTrackFromQueue(track, _tracks.songList())
    }

    fun onClickTrackPlayAll() {
        scope.launch {
            val allSongs = _tracks.songList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }

    fun onPlaybackStateChanged() {
        val currentItems = _tracks.valueOrNull() ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _tracks.value = Resource.Success(updatedList)
    }

    // For iOS
    val tracksFlow: CommonFlow<Resource<List<DisplayableItem>>?>
        get() = tracks.asCommonFlow()
}