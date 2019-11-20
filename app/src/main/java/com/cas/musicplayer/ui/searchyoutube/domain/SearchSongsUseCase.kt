package com.cas.musicplayer.ui.searchyoutube.domain

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.repository.SearchRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 * Copyright © BDSI group BNP Paribas 2019
 *********************************************
 */
class SearchSongsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<MusicTrack>> {
        return repository.searchTracks(query)
    }
}