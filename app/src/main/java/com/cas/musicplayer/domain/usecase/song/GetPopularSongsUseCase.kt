package com.cas.musicplayer.domain.usecase.song

import com.cas.musicplayer.data.repositories.SongsRepository
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.common.result.Result
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetPopularSongsUseCase @Inject constructor(
    private val repository: SongsRepository
) {

    suspend operator fun invoke(max: Int): Result<List<MusicTrack>> {
        return repository.getTrendingSongs(max)
    }
}