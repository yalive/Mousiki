package com.cas.musicplayer.domain.usecase.artist

import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.data.models.Artist
import com.cas.musicplayer.data.repositories.ArtistsRepository
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetArtistSongsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(artist: Artist): Result<List<MusicTrack>> {
        return repository.getArtistTracks(artist)
    }
}