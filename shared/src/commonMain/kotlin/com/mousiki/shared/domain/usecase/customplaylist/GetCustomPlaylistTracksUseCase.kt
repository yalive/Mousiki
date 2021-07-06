package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetCustomPlaylistTracksUseCase(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(playlistName: String): List<Track> {
        return repository.getCustomPlaylistTracks(playlistName)
    }
}