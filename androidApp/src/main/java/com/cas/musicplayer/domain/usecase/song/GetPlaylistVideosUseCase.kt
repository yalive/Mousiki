package com.cas.musicplayer.domain.usecase.song

import com.mousiki.shared.domain.result.Result
import com.cas.musicplayer.data.repositories.PlaylistRepository
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetPlaylistVideosUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<MusicTrack>> {
        return repository.playlistVideos(playlistId)
    }
}