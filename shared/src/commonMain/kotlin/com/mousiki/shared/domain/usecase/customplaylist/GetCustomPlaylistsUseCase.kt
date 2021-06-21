package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.Playlist

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetCustomPlaylistsUseCase(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(): List<Playlist> {
        return repository.getCustomPlaylists()
    }
}