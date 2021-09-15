package com.mousiki.shared.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedVideosFlowUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    operator fun invoke(max: Int = 10): Flow<List<Track>> {
        return statisticsRepository.getRecentlyPlayedVideosFlow(max)
    }
}