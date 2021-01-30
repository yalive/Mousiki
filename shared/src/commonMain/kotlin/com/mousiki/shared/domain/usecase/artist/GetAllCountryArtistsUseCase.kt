package com.mousiki.shared.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.ArtistsRepository

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetAllCountryArtistsUseCase(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(countryCode: String): List<Artist> {
        return repository.getArtistsByCountry(countryCode)
    }

    companion object {
        private const val MAX = 9
    }
}