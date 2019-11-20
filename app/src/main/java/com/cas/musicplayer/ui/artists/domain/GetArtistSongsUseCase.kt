package com.cas.musicplayer.ui.artists.domain

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.repository.ArtistsRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 * Copyright © BDSI group BNP Paribas 2019
 *********************************************
 */
class GetArtistSongsUseCase @Inject constructor(
    private val repository: ArtistsRepository
) {

    suspend operator fun invoke(artistChannelId: String): Result<List<MusicTrack>> {
        return repository.getArtistTracks(artistChannelId)
    }
}