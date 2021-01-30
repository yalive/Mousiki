package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SaveSearchQueryUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String) {
        repository.saveSearchQuery(query)
    }
}