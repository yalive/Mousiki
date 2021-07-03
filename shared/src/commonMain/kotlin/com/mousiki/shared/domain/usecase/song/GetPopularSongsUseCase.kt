package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetPopularSongsUseCase(
    private val repository: SongsRepository
) {

    suspend operator fun invoke(max: Int, lastKnown: YtbTrack? = null): Result<List<YtbTrack>> {
        return repository.getTrendingSongs(max, lastKnown)
    }
}