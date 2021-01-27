package com.cas.musicplayer.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToCustomPlaylistUseCase @Inject constructor(
    private val repository: CustomPlaylistsRepository
) {

    suspend operator fun invoke(track: MusicTrack, playlistName: String) {
        repository.addMusicTrackToCustomPlaylist(track, playlistName)
    }
}