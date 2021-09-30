package com.cas.musicplayer.ui.playlist.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.R
import com.cas.musicplayer.tmp.valueOrNull
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.CustomPlaylistFirstYtbTrackUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksFlowUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */
class CustomPlaylistSongsViewModel(
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getFavouriteTracks: GetFavouriteTracksFlowUseCase,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsFlowUseCase,
    private val getHeavyTracks: GetHeavyTracksFlowUseCase,
    private val getCustomPlaylistFirstYtbTrack: CustomPlaylistFirstYtbTrackUseCase,
    private val getCustomPlaylistTracksFlow: GetCustomPlaylistTracksFlowUseCase,
    private val localSongsRepository: LocalSongsRepository,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {
    lateinit var playlist: Playlist
        private set
    private val _songs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val songs: LiveData<Resource<List<DisplayableItem>>> get() = _songs

    private val _playlistImage = MutableLiveData<AppImage>()
    val playlistImage: LiveData<AppImage> get() = _playlistImage

    fun init(playlist: Playlist) {
        this.playlist = playlist
        getPlaylistSongs()
        prepareHeaderImage()
    }

    private fun getPlaylistSongs() = viewModelScope.launch {
        _songs.value = Resource.Loading
        when (playlist.type) {
            Playlist.TYPE_FAV -> getFavouriteTracks(300).collect { tracksMapper(it) }
            Playlist.TYPE_RECENT -> getRecentlyPlayedSongs(300).collect { tracksMapper(it) }
            Playlist.TYPE_HEAVY -> getHeavyTracks(300).collect { tracksMapper(it) }
            else -> getCustomPlaylistTracksFlow(playlist.id).collect { tracksMapper(it) }
        }
    }

    private suspend fun tracksMapper(tracks: List<Track>) {
        val mappedTracks = tracks.map {
            when (it) {
                is LocalSong -> LocalSong(localSongsRepository.song(it.song.id))
                is YtbTrack -> it
            }
        }
        showTracks(mappedTracks)
    }

    private fun prepareHeaderImage() {
        if (!playlist.isFavourite && !playlist.isRecent && !playlist.isHeavy) {
            viewModelScope.launch {
                _playlistImage.value = AppImage.AppImageUrl(playlist.withImage().urlImage)
            }
        } else {
            val drawable = when (playlist.type) {
                Playlist.TYPE_FAV -> R.drawable.fav_playlist
                Playlist.TYPE_HEAVY -> R.drawable.most_played_playlist
                Playlist.TYPE_RECENT -> R.drawable.recently_played_playlist
                else -> -1
            }
            _playlistImage.value = AppImage.AppImageRes(drawable, true)
        }
    }

    private fun showTracks(tracks: List<Track>) {
        val items = tracks.map {
            val isCurrent = currentSong?.id == it.id
            it.toDisplayedVideoItem(
                isCurrent = isCurrent,
                isPlaying = isCurrent && isPlayingASong()
            )
        }
        _songs.value = Resource.Success(items)
    }

    fun onClickTrack(track: Track) = viewModelScope.launch {
        val tracks = (_songs.value as? Resource.Success)?.data
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickTrackPlayAll() {
        viewModelScope.launch {
            val allSongs = _songs.valueOrNull()
                ?.filterIsInstance<DisplayedVideoItem>() ?: emptyList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }

    fun onClickShufflePlay() {
        viewModelScope.launch {
            val allSongs = _songs.valueOrNull()
                ?.filterIsInstance<DisplayedVideoItem>()?.shuffled() ?: emptyList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }

    fun refresh() {
        getPlaylistSongs()
    }

    fun onPlaybackStateChanged() {
        val currentItems = _songs.valueOrNull() ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _songs.value = Resource.Success(updatedList)
    }

    private suspend fun Playlist.withImage(): Playlist {
        return if (isCustom) {
            val ytbTrack = getCustomPlaylistFirstYtbTrack(id)
            copy(urlImage = ytbTrack?.imgUrlDef0.orEmpty())
        } else this
    }

    fun isRecentlyPlayed() = playlist.type == Playlist.TYPE_RECENT
}