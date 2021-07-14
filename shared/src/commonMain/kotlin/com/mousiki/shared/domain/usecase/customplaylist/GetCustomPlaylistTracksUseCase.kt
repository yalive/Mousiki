package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetCustomPlaylistTracksUseCase(
    private val repository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistId: String): List<Track> {
        return repository.getCustomPlaylistTracks(playlistId.toLong())
    }
}