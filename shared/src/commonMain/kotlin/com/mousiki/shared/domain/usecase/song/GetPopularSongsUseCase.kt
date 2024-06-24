package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetPopularSongsUseCase(
    //private val repository: SongsRepository,
    private val homeRepository: HomeRepository
) {

    /* suspend operator fun invoke(max: Int, lastKnown: YtbTrack? = null): Result<List<YtbTrack>> {
         return repository.getTrendingSongs(max, lastKnown)
     }*/

    suspend operator fun invoke(max: Int, lastKnown: AiTrack? = null): Result<List<AiTrack>> =
        homeRepository.getTrending().map {
            it.data.map { song ->
                AiTrack(song)
            }
        }
}