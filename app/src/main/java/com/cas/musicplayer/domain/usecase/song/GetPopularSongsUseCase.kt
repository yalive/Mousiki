package com.cas.musicplayer.domain.usecase.song

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SongsRepository
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetPopularSongsUseCase @Inject constructor(
    private val repository: SongsRepository
) {

    suspend operator fun invoke(max: Int, lastKnown: MusicTrack? = null): Result<List<MusicTrack>> {
        return repository.getTrendingSongs(max, lastKnown)
    }
}