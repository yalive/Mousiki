package com.cas.musicplayer.domain.usecase.genre

import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.domain.models.GenreMusic
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetGenresUseCase @Inject constructor(
    private val repository: GenresRepository
) {
    suspend operator fun invoke(): List<GenreMusic> {
        return repository.loadGenres()
    }
}