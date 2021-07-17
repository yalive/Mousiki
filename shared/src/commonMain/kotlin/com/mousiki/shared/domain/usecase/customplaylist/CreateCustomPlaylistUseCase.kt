package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreateCustomPlaylistUseCase(
    private val repository: PlaylistsRepository
) {
    suspend operator fun invoke(name: String, description: String = ""): CreatePlaylistResult {
        return when (repository.playlistWithNameExist(name)) {
            true -> CreatePlaylistResult.NameAlreadyExist
            false -> {
                val playlist = repository.createCustomPlaylist(name, description)
                CreatePlaylistResult.Created(playlist)
            }
        }
    }
}

sealed class CreatePlaylistResult {
    data class Created(val playlist: Playlist) : CreatePlaylistResult()
    object NameAlreadyExist : CreatePlaylistResult()
}