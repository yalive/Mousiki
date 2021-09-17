package com.mousiki.shared.domain.usecase.song

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetYtbSongUseCase(
    private val repository: SongsRepository
) {

    suspend operator fun invoke(id: String): Result<Track> {
        return repository.getSong(id)
    }
}