package com.cas.musicplayer.domain.usecase.search

import com.cas.musicplayer.data.repositories.SearchRepository
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class GetRecentSearchQueriesUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String): List<String> = withContext(bgContext) {
        return@withContext repository.searchRecentQueries(keyword)
    }
}