package com.cas.musicplayer.domain.usecase.artist

import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.data.models.Artist
import com.cas.musicplayer.data.repositories.ArtistsRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetCountryArtistsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(countryCode: String): Result<List<Artist>> {
        val artists = repository.getArtistsByCountry(countryCode)
        var sixArtist = artists.shuffled().take(MAX)
        if (sixArtist.size < MAX) {
            // Request Global
            val globalArtists = repository.getArtistsByCountry("GLOBAL")
            sixArtist = globalArtists.shuffled().take(MAX)
        }
        return Result.Success(sixArtist)
    }

    companion object {
        private const val MAX = 9
    }
}