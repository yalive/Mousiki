package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.PlaylistRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetPlaylistVideosUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<MusicTrack>> {
        return repository.playlistVideos(playlistId)
    }
}