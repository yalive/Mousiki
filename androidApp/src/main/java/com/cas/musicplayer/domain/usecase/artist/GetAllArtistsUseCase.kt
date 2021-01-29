package com.cas.musicplayer.domain.usecase.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.repository.ArtistsRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetAllArtistsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(): List<Artist> {
        return repository.getAllArtists()
    }
}