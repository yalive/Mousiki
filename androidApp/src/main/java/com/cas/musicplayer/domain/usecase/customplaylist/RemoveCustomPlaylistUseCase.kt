package com.cas.musicplayer.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class RemoveCustomPlaylistUseCase @Inject constructor(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(playlistName: String) {
        repository.deleteCustomPlaylist(playlistName)
    }
}