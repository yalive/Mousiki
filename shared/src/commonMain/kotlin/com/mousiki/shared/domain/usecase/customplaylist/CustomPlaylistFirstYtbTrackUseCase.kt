package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CustomPlaylistFirstYtbTrackUseCase(
    private val repository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistId: String): Track? {
        return repository.getCustomPlaylistFirstYtbTrack(playlistId.toLong())
    }
}