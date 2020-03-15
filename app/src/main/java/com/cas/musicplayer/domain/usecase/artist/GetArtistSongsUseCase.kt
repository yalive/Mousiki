package com.cas.musicplayer.domain.usecase.artist

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.ArtistsRepository
import com.cas.musicplayer.domain.model.MusicTrack
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

    suspend operator fun invoke(artistChannelId: String): Result<List<MusicTrack>> {
        return repository.getArtistTracks(artistChannelId)
    }
}