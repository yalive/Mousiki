package com.mousiki.shared.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetArtistSongsUseCase(
    //private val repository: ArtistsRepository
    private val homeRepository: HomeRepository
) {

    /*suspend operator fun invoke(artist: Artist): Result<List<YtbTrack>> {
        return repository.getArtistTracks(artist)
    }*/
    suspend operator fun invoke(artist: Artist): Result<List<AiTrack>> {
        return homeRepository.getUserSongs(artist).map {
            it.data.map { song->
                AiTrack(song)
            }
        }
    }
}