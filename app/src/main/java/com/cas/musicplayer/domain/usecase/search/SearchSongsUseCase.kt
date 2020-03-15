package com.cas.musicplayer.domain.usecase.search

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SearchRepository
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchSongsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<MusicTrack>> {
        return repository.searchTracks(query)
    }
}