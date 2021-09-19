package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.StatisticsRepository

class RemoveVideoFromRecentlyPlayedUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(trackId: String) {
        statisticsRepository.removeVideoFromRecent(trackId)
    }
}