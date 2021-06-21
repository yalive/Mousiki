package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class GetRecentSearchQueriesUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String): List<String> {
        return repository.searchRecentQueries(keyword).reversed()
    }
}