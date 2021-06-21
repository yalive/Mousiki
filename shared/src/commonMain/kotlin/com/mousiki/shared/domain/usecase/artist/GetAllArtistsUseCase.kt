package com.mousiki.shared.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.ArtistsRepository

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetAllArtistsUseCase(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(): List<Artist> {
        return repository.getAllArtists()
    }
}