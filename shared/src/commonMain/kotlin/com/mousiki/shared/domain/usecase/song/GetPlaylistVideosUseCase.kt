package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.YtbPlaylistRepository
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetPlaylistVideosUseCase(
    private val repositoryYtb: YtbPlaylistRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<YtbTrack>> {
        return repositoryYtb.playlistVideos(playlistId)
    }
}