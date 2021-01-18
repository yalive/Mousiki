package com.cas.musicplayer.ui.artists.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.artist.GetArtistSongsUseCase
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
class ArtistSongsViewModel @AssistedInject constructor(
    @Assisted private val artist: Artist,
    private val getArtistSongs: GetArtistSongsUseCase,
    playDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playDelegate, GetListAdsDelegate by getListAdsDelegate {

    private val _tracks = MutableLiveData<Resource<List<DisplayableItem>>>()
    val tracks: LiveData<Resource<List<DisplayableItem>>>
        get() = _tracks

    @AssistedInject.Factory
    interface Factory {
        fun create(artist: Artist): ArtistSongsViewModel
    }

    init {
        loadArtistTracks(artist)
    }

    fun loadArtistTracks(artist: Artist) = uiCoroutine {
        _tracks.value = Resource.Loading
        val result = getArtistSongs(artist)
        _tracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        populateAdsIn(_tracks)
    }

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, _tracks.songList())
    }

    fun onClickTrackPlayAll() {
        uiCoroutine {
            val allSongs = _tracks.songList()
            if (allSongs.isEmpty()) return@uiCoroutine
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }
}