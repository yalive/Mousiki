package com.mousiki.shared.ui.artist.songs

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetArtistSongsUseCase
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
class ArtistSongsViewModel(
    private val getArtistSongs: GetArtistSongsUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _tracks = MutableStateFlow<Resource<List<DisplayableItem>>?>(null)
    val tracks: StateFlow<Resource<List<DisplayableItem>>?>
        get() = _tracks

    fun init(artist: Artist) {
        loadArtistTracks(artist)
    }

    fun loadArtistTracks(artist: Artist) = scope.launch {
        _tracks.value = Resource.Loading
        val result = getArtistSongs(artist)
        _tracks.value = result.map { tracks ->
            tracks.map {
                val isCurrent = currentSong?.youtubeId == it.youtubeId
                it.toDisplayedVideoItem(
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlayingASong()
                )
            }
        }.asResource()
        populateAdsIn(_tracks)
    }

    fun onClickTrack(track: MusicTrack) = scope.launch {
        playTrackFromQueue(track, _tracks.songList())
    }

    fun onClickTrackPlayAll() {
        scope.launch {
            val allSongs = _tracks.songList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }

    fun updateCurrentPlayingItem() {
        val resource = _tracks.value ?: return
        val currentItems = (resource as? Resource.Success)?.data ?: return
        val updatedList = currentItems.map { item ->
            when (item) {
                is DisplayedVideoItem -> {
                    val isCurrent = currentSong?.youtubeId == item.track.youtubeId
                    item.copy(
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlayingASong()
                    )
                }
                else -> item
            }
        }
        _tracks.value = Resource.Success(updatedList)
    }

    // For iOS
    val tracksFlow: CommonFlow<Resource<List<DisplayableItem>>?>
        get() = tracks.asCommonFlow()
}