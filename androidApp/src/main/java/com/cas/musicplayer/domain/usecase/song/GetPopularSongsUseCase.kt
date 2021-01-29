package com.cas.musicplayer.domain.usecase.song

import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.data.repository.SongsRepository
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