package com.cas.musicplayer.ui.playlist.create

import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.customplaylist.CreateCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.customplaylist.CreatePlaylistResult
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.utils.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel(
    private val createCustomPlaylist: CreateCustomPlaylistUseCase,
    private val strings: Strings
) : BaseViewModel() {

    private val _playlistCreated = MutableStateFlow<Event<Playlist>?>(null)
    val playlistCreated: StateFlow<Event<Playlist>?> = _playlistCreated

    fun createPlaylist(playlistName: String) {
        if (playlistName.isEmpty()) return
        scope.launch {
            when (val result = createCustomPlaylist(playlistName)) {
                is CreatePlaylistResult.Created -> {
                    showToast(strings.playlistCreated(playlistName))
                    _playlistCreated.value = result.playlist.asEvent()
                }
                CreatePlaylistResult.NameAlreadyExist -> showToast(
                    strings.playlistExist(playlistName)
                )
            }
        }
    }
}