package com.mousiki.shared.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.ArtistsRepository
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetArtistSongsUseCase(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(artist: Artist): Result<List<YtbTrack>> {
        return repository.getArtistTracks(artist)
    }
}