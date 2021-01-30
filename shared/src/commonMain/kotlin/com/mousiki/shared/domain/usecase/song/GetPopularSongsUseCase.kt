package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetPopularSongsUseCase(
    private val repository: SongsRepository
) {

    suspend operator fun invoke(max: Int, lastKnown: MusicTrack? = null): Result<List<MusicTrack>> {
        return repository.getTrendingSongs(max, lastKnown)
    }
}