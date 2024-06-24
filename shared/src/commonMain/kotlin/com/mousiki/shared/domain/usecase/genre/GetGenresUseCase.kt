package com.mousiki.shared.domain.usecase.genre

import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.domain.models.GenreMusic

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetGenresUseCase(
    private val repository: GenresRepository
) {
    suspend operator fun invoke(): List<GenreMusic> {
        return repository.loadAiGenres()
    }
}