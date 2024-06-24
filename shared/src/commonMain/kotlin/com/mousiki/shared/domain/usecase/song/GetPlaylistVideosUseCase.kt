package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetPlaylistVideosUseCase(
    //private val repositoryYtb: YtbPlaylistRepository
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<AiTrack>> {
        return homeRepository.getSongs(playlistId)
            .map {
                it.songs.map { song ->
                    AiTrack(song)
                }
            }
    }
}