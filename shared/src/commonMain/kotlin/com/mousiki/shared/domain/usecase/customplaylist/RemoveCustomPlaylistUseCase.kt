package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class RemoveCustomPlaylistUseCase(
    private val repository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistId: String) {
        repository.deleteCustomPlaylist(playlistId.toLong())
    }
}