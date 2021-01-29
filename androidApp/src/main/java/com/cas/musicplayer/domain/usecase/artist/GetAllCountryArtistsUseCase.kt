package com.cas.musicplayer.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.ArtistsRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetAllCountryArtistsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(countryCode: String): List<Artist> {
        val artists = repository.getArtistsByCountry(countryCode)
        return artists
    }

    companion object {
        private const val MAX = 9
    }
}