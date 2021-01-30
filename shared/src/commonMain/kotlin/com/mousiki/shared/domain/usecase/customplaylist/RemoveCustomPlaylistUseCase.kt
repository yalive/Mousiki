package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class RemoveCustomPlaylistUseCase(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(playlistName: String) {
        repository.deleteCustomPlaylist(playlistName)
    }
}