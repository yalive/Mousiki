package com.cas.musicplayer.ui.home.domain.usecase

import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetGenresUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): List<GenreMusic> {
        return repository.loadGenres()
    }
}