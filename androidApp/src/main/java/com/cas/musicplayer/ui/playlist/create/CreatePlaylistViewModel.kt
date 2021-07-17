package com.cas.musicplayer.ui.playlist.create

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.customplaylist.CreateCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.customplaylist.CreatePlaylistResult
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel(
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val createCustomPlaylist: CreateCustomPlaylistUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {

    fun createPlaylist(track: Track?, playlistName: String) = viewModelScope.launch {
        when (val result = createCustomPlaylist(playlistName)) {
            is CreatePlaylistResult.Created -> {
                if (track == null) return@launch
                addTrackToCustomPlaylist.invoke(track, result.playlist.id.toLong())
                showToast("Added to $playlistName")
            }
            CreatePlaylistResult.NameAlreadyExist -> showToast("Playlist $playlistName existed")
        }
    }
}