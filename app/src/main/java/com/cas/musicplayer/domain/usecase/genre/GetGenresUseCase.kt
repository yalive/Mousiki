package com.cas.musicplayer.domain.usecase.genre

import com.cas.musicplayer.data.repositories.GenresRepository
import com.cas.musicplayer.domain.model.GenreMusic
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