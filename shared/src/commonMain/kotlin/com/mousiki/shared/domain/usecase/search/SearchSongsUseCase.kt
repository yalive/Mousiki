package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository
import com.mousiki.shared.domain.models.SearchTracksResult
import com.mousiki.shared.domain.result.Result

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchSongsUseCase(
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

