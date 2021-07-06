package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class DeleteTrackFromCustomPlaylistUseCase(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(track: Track, playlistName: String) {
        repository.deleteTrackFromCustomPlaylist(track, playlistName)
    }
}