package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetLocalPlaylistsUseCase(
    private val repository: PlaylistsRepository
) {
    suspend operator fun invoke(): List<Playlist> {
        return repository.getPlaylists()
    }
}