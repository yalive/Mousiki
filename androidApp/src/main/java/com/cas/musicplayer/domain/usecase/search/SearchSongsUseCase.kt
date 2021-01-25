package com.cas.musicplayer.domain.usecase.search

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SearchRepository
import com.mousiki.shared.domain.models.SearchTracksResult
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
    suspend operator fun invoke(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<SearchTracksResult> {
        return repository.searchTracks(query, key, token)
    }
}

