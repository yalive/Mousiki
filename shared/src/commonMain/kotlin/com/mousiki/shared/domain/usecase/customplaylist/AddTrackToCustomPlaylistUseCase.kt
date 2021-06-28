package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToCustomPlaylistUseCase(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(track: YtbTrack, playlistName: String) {
        repository.addMusicTrackToCustomPlaylist(track, playlistName)
    }
}