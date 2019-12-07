package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksLiveUseCase
import com.cas.musicplayer.domain.usecase.library.GetHeavyTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsLiveUseCase
import com.cas.musicplayer.ui.BaseSongsViewModel
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryViewModel @Inject constructor(
    private val getRecentlyPlayedSongsLive: GetRecentlyPlayedSongsLiveUseCase,
    private val getHeavyTracks: GetHeavyTracksUseCase,
    private val getFavouriteTracksLive: GetFavouriteTracksLiveUseCase,
    addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : BaseSongsViewModel(addTrackToRecentlyPlayed) {

    private val _recentSongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>> = _recentSongs

    private val _heavySongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val heavySongs: LiveData<List<DisplayedVideoItem>> = _heavySongs

    private val _favouriteSongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val favouriteSongs: LiveData<List<DisplayedVideoItem>> = _favouriteSongs

    init {
        uiCoroutine {

            _favouriteSongs.addSource(getFavouriteTracksLive(10)) { songs ->
                _favouriteSongs.postValue(tracksToDisplayableItems(songs))
            }

            _recentSongs.addSource(getRecentlyPlayedSongsLive(10)) { songs ->
                _recentSongs.postValue(tracksToDisplayableItems(songs))
            }

            _heavySongs.addSource(getHeavyTracks(10)) { songs ->
                _heavySongs.postValue(tracksToDisplayableItems(songs))
            }
        }
    }


    fun onClickRecentTrack(track: MusicTrack) {
        val tracks = _recentSongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickHeavyTrack(track: MusicTrack) {
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickFavouriteTrack(track: MusicTrack) {
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    private fun tracksToDisplayableItems(songs: List<MusicTrack>) = songs.map { it.toDisplayedVideoItem() }
}