package com.cas.musicplayer.domain.usecase.customplaylist

import com.cas.musicplayer.data.repositories.CustomPlaylistsRepository
import com.cas.musicplayer.domain.model.Playlist
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