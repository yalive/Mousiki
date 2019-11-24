package com.cas.musicplayer.domain.usecase.artist

import com.cas.musicplayer.data.remote.models.Artist
import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.ArtistsRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 * Copyright © BDSI group BNP Paribas 2019
 *********************************************
 */
class GetArtistsThumbnailsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(ids: List<String>): Result<List<Artist>> {
        return repository.getArtistsChannels(ids)
    }
}