package com.cas.musicplayer.domain.usecase.customplaylist

import com.cas.musicplayer.data.repositories.CustomPlaylistsRepository
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class DeleteTrackFromCustomPlaylistUseCase @Inject constructor(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(track: MusicTrack, playlistName: String) {
        repository.deleteTrackFromCustomPlaylist(track, playlistName)
    }
}