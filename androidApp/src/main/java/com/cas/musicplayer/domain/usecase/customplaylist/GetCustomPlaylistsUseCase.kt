package com.cas.musicplayer.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.Playlist
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetCustomPlaylistsUseCase @Inject constructor(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(): List<Playlist> {
        return repository.getCustomPlaylists()
    }
}