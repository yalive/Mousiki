package com.cas.musicplayer.domain.usecase.artist

import com.cas.common.result.Result
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.repositories.ArtistsRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetCountryArtistsUseCase @Inject constructor(
    private val repository: ArtistsRepository,
    private val getArtistsThumbnails: GetArtistsThumbnailsUseCase
) {

    suspend operator fun invoke(countryCode: String): Result<List<Artist>> {
        val artists = repository.getArtistsFromFile()
        // Filter 6 artist by country
        var sixArtist = artists.filter {
            it.countryCode.equals(countryCode, true)
        }.shuffled().take(MAX)

        if (sixArtist.size < MAX) {
            // Request US
            sixArtist = artists.filter {
                it.countryCode.equals("US", true)
            }.shuffled().take(MAX)
        }

        // Get detail of artists
        val ids = sixArtist.map { it.channelId }
        return getArtistsThumbnails(ids)
    }

    companion object {
        private const val MAX = 9
    }
}