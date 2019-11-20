package com.cas.musicplayer.ui.artists.domain

import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.repository.ArtistsRepository
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

    suspend operator fun invoke(ids: String): Result<List<Artist>> {
        return repository.getArtistsChannels(ids)
    }
}