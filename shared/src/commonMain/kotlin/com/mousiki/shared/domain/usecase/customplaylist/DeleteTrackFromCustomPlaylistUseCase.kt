package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class DeleteTrackFromCustomPlaylistUseCase(
    private val repository: PlaylistsRepository
) {

    suspend operator fun invoke(track: Track, playlistId: String) {
        repository.deleteTrackFromCustomPlaylist(track, playlistId.toLong())
    }
}