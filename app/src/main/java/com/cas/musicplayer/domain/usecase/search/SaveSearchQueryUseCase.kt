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
class SaveSearchQueryUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String) = withContext(bgContext) {
        repository.saveSearchQuery(query)
    }
}